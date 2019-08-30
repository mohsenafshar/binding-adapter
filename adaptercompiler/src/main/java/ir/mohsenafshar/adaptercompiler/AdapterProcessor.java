package ir.mohsenafshar.adaptercompiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import ir.mohsenafshar.adapterannotation.AdapterAnnotation;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "ir.mohsenafshar.adapterannotation.AdapterAnnotation"
})
@AutoService(Processor.class)
public class AdapterProcessor extends AbstractProcessor {

    private static final ClassName classContext = ClassName.get("android.content", "Context");
    private static final ClassName classBundle = ClassName.get("android.os", "Bundle");
    private static final ClassName classAdapter = ClassName.get("androidx.recyclerview.widget", "Adapter");

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;

    private String prefix;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotataions = new LinkedHashSet<String>();
        annotataions.add(AdapterAnnotation.class.getCanonicalName());
        return annotataions;
    }

    /*
    *   Step1 :get name of class that adapterAnnotation is used in
    *       and use this value to create adapter class name for example :
    *       {TicketActivity - Activity + Adapter = TicketAdapter}
    *
    * Step 2:
    *       get viewHolderClass Name for useing in generic type of recyclerViewAdapter
    *       like this: TicketAdapter extends RecyclerView.Adapter<TicketViewHolder>

    * Step 3:
    *       create field with type of ArrayList<T> where T should be capture from field
    *       that AdapterAnnotation is used on
    *
    * Step 4:
    *       create constructor that receives the arrayList<T>
    *
    * */

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        try {
            for (Element element : roundEnvironment.getElementsAnnotatedWith(AdapterAnnotation.class)) {
                if (element.getKind() != ElementKind.CLASS) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "it's just can applied to CLASS");
                    return true;
                }

                // get supertype
                TypeMirror typeMirror = element.asType();
                List<? extends TypeMirror> mirrors = typeUtils.directSupertypes(typeMirror);
                for (TypeMirror mirror : mirrors) {
                    DeclaredType declared = (DeclaredType)mirror; //you should of course check this is possible first
                    Element supertypeElement = declared.asElement();
                    System.out.println( "Supertype name: " + supertypeElement.getSimpleName() );
                    messager.printMessage(Diagnostic.Kind.WARNING, supertypeElement.getSimpleName());
                }

                messager.printMessage(Diagnostic.Kind.WARNING, element.getSimpleName());

                String string = element.getSimpleName().toString();
                if (string.contains("Activity")) {
                    prefix = string.replace("Activity", "");
                } else if (string.contains("Fragment")) {
                    prefix = string.replace("Fragment", "");
                }

                /*TypeElement typeElement = (TypeElement) element;
                activitiesWithPackage.put(
                        typeElement.getSimpleName().toString(),
                        elements.getPackageOf(typeElement).getQualifiedName().toString());

                Navigate navigate = typeElement.getAnnotation(Navigate.class);

                TypeSpec.Builder navigatorClass = TypeSpec.classBuilder("Navigator")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL);


                builder.addStatement("$L.startActivity(intent)", "context");
                MethodSpec intentMethod = builder.build();
                navigatorClass.addMethod(intentMethod);*/
            }


            /*
             * 3- Write generated class to a file
             */
//            JavaFile.builder("ir.mohsenafshar.activitynavigator", navigatorClass.build()).build().writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
