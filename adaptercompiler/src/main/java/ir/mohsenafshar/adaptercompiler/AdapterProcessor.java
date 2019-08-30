package ir.mohsenafshar.adaptercompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import ir.mohsenafshar.adapterannotation.AdapterAnnotation;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "ir.mohsenafshar.adapterannotation.AdapterAnnotation"
})
public class AdapterProcessor extends AbstractProcessor {

    private static final ClassName classContext = ClassName.get("android.content", "Context");
    private static final ClassName classBundle = ClassName.get("android.os", "Bundle");
    private static final ClassName classAdapter = ClassName.get("androidx.recyclerview.widget", "Adapter");

    private Filer filer;
    private Messager messager;
    private Elements elements;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
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

                TypeElement typeElement = (TypeElement) element;
                activitiesWithPackage.put(
                        typeElement.getSimpleName().toString(),
                        elements.getPackageOf(typeElement).getQualifiedName().toString());

                Navigate navigate = typeElement.getAnnotation(Navigate.class);

                TypeSpec.Builder navigatorClass = TypeSpec.classBuilder("Navigator")
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL);


                builder.addStatement("$L.startActivity(intent)", "context");
                MethodSpec intentMethod = builder.build();
                navigatorClass.addMethod(intentMethod);
            }


            /*
             * 3- Write generated class to a file
             */
            JavaFile.builder("ir.mohsenafshar.activitynavigator", navigatorClass.build()).build().writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
