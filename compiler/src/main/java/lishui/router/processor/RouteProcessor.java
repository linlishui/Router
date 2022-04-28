package lishui.lib.router.processor;

import static javax.lang.model.element.Modifier.PUBLIC;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import lishui.lib.router.annotation.Route;
import lishui.lib.router.annotation.enums.RouteType;
import lishui.lib.router.annotation.model.RouteMeta;
import lishui.lib.router.processor.utils.Configs;

/**
 * A processor used for find route.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({Configs.ANNOTATION_TYPE_ROUTE})
public class RouteProcessor extends BaseProcessor {

    // ModuleName and routeMeta.
    private final Map<String, Set<RouteMeta>> groupMap = new HashMap<>();
    // Map of root metas, used for generate class file in order.
    private final Map<String, String> rootMap = new TreeMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        logger.info(">>> RouteProcessor init. <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnv) {
        if (CollectionUtils.isNotEmpty(typeElements)) {
            // 获取被Route注解的元素节点，这里Route定义在类元素上
            Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
            try {
                logger.info(">>> Found routes, start... <<<");
                this.parseRoutes(routeElements);

            } catch (Exception e) {
                logger.error(e);
            }
            return true;
        }
        return false;
    }

    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        if (CollectionUtils.isEmpty(routeElements)) {
            return;
        }

        logger.info(">>> Found routes, size is " + routeElements.size() + " <<<");

        rootMap.clear();

        TypeMirror activityType = elementUtils.getTypeElement(Configs.ACTIVITY).asType();
        TypeMirror serviceType = elementUtils.getTypeElement(Configs.SERVICE).asType();

        // Interface of Router
        TypeElement typeIRouteGroup = elementUtils.getTypeElement(Configs.IROUTE_GROUP);
        ClassName routeTypeCN = ClassName.get(RouteType.class);
        ClassName routeMetaCN = ClassName.get(RouteMeta.class);

        /*
           Build input type, format as :
           ```Map<String, Class<? extends IRouteGroup>>```
         */
        ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ParameterizedTypeName.get(
                        ClassName.get(Class.class),
                        WildcardTypeName.subtypeOf(ClassName.get(typeIRouteGroup))
                )
        );

        // ```Map<String, RouteMetaData>```
        ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                ClassName.get(Map.class),
                ClassName.get(String.class),
                ClassName.get(RouteMeta.class)
        );

        // Build input param name.
        ParameterSpec rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routes").build();
        ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build();

        // Build method : 'loadInto'
        MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(Configs.METHOD_LOAD_INTO)
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .addParameter(rootParamSpec);

        //  Follow a sequence, find out route data, generate java file, then statistics them as root.
        for (Element element : routeElements) {
            // 1. 拿到被注解的类型映射和该注解实例
            TypeMirror typeMirror = element.asType();
            Route route = element.getAnnotation(Route.class);

            RouteMeta routeMeta;
            // 2. 判断当前被注解的元素类型，封装Route注解里的数据
            if (types.isSubtype(typeMirror, activityType)) {
                logger.info(">>> Found activity route: " + typeMirror.toString() + " <<<");
                routeMeta = new RouteMeta(route, element, RouteType.ACTIVITY);
            } else if (types.isSubtype(typeMirror, serviceType)) {
                logger.info(">>> Found service route: " + typeMirror.toString() + " <<<");
                routeMeta = new RouteMeta(route, element, RouteType.SERVICE);
            } else {
                throw new RuntimeException("The @Route is marked on unsupported class, look at [" + typeMirror.toString() + "].");
            }
            // 3. 根据组名或一级路径名作为分类的名称，并将注解数据存入给由groupMap进行存储
            categories(routeMeta);
        }

        // Start generate java source, structure is divided into upper and lower levels, used for demand initialization.
        for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
            String groupName = entry.getKey();

            MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(Configs.METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(groupParamSpec);

            // Build group method body
            Set<RouteMeta> groupData = entry.getValue();
            for (RouteMeta routeMeta : groupData) {

                ClassName className = ClassName.get((TypeElement) routeMeta.getRawType());

                loadIntoMethodOfGroupBuilder.addStatement(
                        "atlas.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, " + routeMeta.getPriority() + "))",
                        routeMeta.getPath(),
                        routeMetaCN,
                        routeTypeCN,
                        className,
                        routeMeta.getPath().toLowerCase(),
                        routeMeta.getGroup().toLowerCase());
            }

            // Generate groups
            String groupFileName = Configs.NAME_OF_GROUP + groupName;
            JavaFile.builder(Configs.PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(groupFileName)
                            .addJavadoc(Configs.WARNING_TIPS)
                            .addSuperinterface(ClassName.get(typeIRouteGroup))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfGroupBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            logger.info(">>> Generated group: " + groupName + " <<<");
            rootMap.put(groupName, groupFileName);
        }

        if (MapUtils.isNotEmpty(rootMap)) {
            // Generate root meta by group name, it must be generated before root, then I can find out the class of group.
            for (Map.Entry<String, String> entry : rootMap.entrySet()) {
                loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(Configs.PACKAGE_OF_GENERATE_FILE, entry.getValue()));
            }
        }

        // Write root meta into disk.
        String rootFileName = Configs.NAME_OF_ROOT + Configs.SEPARATOR + moduleName;
        JavaFile.builder(Configs.PACKAGE_OF_GENERATE_FILE,
                TypeSpec.classBuilder(rootFileName)
                        .addJavadoc(Configs.WARNING_TIPS)
                        .addSuperinterface(ClassName.get(elementUtils.getTypeElement(Configs.ITROUTE_ROOT)))
                        .addModifiers(PUBLIC)
                        .addMethod(loadIntoMethodOfRootBuilder.build())
                        .build()
        ).build().writeTo(mFiler);

        logger.info(">>> Generated root, name is " + rootFileName + " <<<");
    }

    /**
     * Sort metas in group.
     *
     * @param routeMete metas.
     */
    private void categories(RouteMeta routeMete) {
        if (routeVerify(routeMete)) {
            logger.info(">>> Start categories, group = " + routeMete.getGroup() + ", path = " + routeMete.getPath() + " <<<");
            Set<RouteMeta> routeMetas = groupMap.get(routeMete.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                Set<RouteMeta> routeMetaSet = new TreeSet<>((r1, r2) -> {
                    try {
                        return r1.getPath().compareTo(r2.getPath());
                    } catch (NullPointerException npe) {
                        logger.error(npe.getMessage());
                        return 0;
                    }
                });
                routeMetaSet.add(routeMete);
                groupMap.put(routeMete.getGroup(), routeMetaSet);
            } else {
                routeMetas.add(routeMete);
            }
        } else {
            logger.warning(">>> Route meta verify error, group is " + routeMete.getGroup() + " <<<");
        }
    }

    /**
     * Verify the route meta
     *
     * @param meta raw meta
     */
    private boolean routeVerify(RouteMeta meta) {
        String path = meta.getPath();

        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {   // The path must be start with '/' and not empty!
            return false;
        }

        if (StringUtils.isEmpty(meta.getGroup())) { // Use default group(the first word in path)
            try {
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                if (StringUtils.isEmpty(defaultGroup)) {
                    return false;
                }

                meta.setGroup(defaultGroup);
                return true;
            } catch (Exception e) {
                logger.error("Failed to extract default group! " + e.getMessage());
                return false;
            }
        }

        return true;
    }
}
