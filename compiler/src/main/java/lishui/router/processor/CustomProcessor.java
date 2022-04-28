package lishui.lib.router.processor;

import java.util.Set;

import javax.lang.model.element.Element;


public abstract class CustomProcessor extends BaseProcessor {

    private void parseCustom(Set<? extends Element> customElements) {
        logger.info("----- parseCustom begin -----");
        for (Element element : customElements) {
            logger.info("simpleName=" + element.getSimpleName());
            logger.info("asType=" + element.asType().toString());
            logger.info("kind=" + element.getKind().name());
            logger.info("modifiers=" + element.getModifiers().toString());
            Element parentElement = element.getEnclosingElement();
            if (parentElement != null) {
                logger.info("parent simpleName=" + parentElement.getSimpleName());
                logger.info("parent asType=" + parentElement.asType().toString());
            }
            /*Custom custom = element.getAnnotation(Custom.class);
            logger.info(custom.toString());*/
            element.getAnnotationMirrors().forEach(consumer -> {
                logger.info("annotationMirror kind=" + consumer.getAnnotationType().getKind().name());
                logger.info("annotationMirror elementValues=" + consumer.getElementValues().toString());
                //logger.info("annotationMirror=" + consumer.toString());
            });
            for (Element field : element.getEnclosedElements()) {
                logger.info("enclosed element name=" + field.getSimpleName());
                logger.info("enclosed element type=" + field.asType().toString());
                logger.info("enclosed element kind=" + field.getKind().name());
                logger.info("enclosed element modifiers=" + field.getModifiers().toString());
            }
            logger.info("----------");
        }
        logger.info("----- parseCustom end -----");
    }
}
