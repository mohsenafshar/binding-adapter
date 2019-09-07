package ir.mohsenafshar.adaptercompiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.LinkedHashSet;
import java.util.List;
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
import javax.lang.model.type.MirroredTypeException;
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
    private static final ClassName classInflater = ClassName.get("ir.mohsenafshar.sample", "LayInflater");

    private static final ClassName classContext = ClassName.get("android.content", "Context");
    private static final ClassName classList = ClassName.get("android.content", "Context");
    private static final ClassName classBundle = ClassName.get("android.os", "Bundle");
    private static final ClassName list = ClassName.get("java.util", "List");
    private static final ClassName arrayList = ClassName.get("java.util", "ArrayList");
    private static final ClassName classInteger = ClassName.get("java.lang", "Integer");
    private static final ClassName classView = ClassName.get("android.view", "View");
    private static final ClassName classViewGroup = ClassName.get("android.view", "ViewGroup");
    private static final ClassName classLayoutInflator = ClassName.get("android.view", "LayoutInflater");
    private static final ClassName classOverrideAnnotation = ClassName.get("java.lang", "Override");
    private static final ClassName classString = ClassName.get("java.lang", "String");
    private static final ClassName classAdapter = ClassName.get("androidx.recyclerview.widget", "RecyclerView.Adapter");

    private static final ClassName recyclerViewClass = ClassName.get("androidx.recyclerview.widget", "RecyclerView");
    private static final ClassName itemClickClass = ClassName.get("ir.mohsenafshar.listener", "ItemClickListener");
    private static final ClassName itemLongClass = ClassName.get("ir.mohsenafshar.listener", "ItemLongClickListener");
//    private static final ClassName classAdapter = ClassName.get("androidx.recyclerview.widget", "Adapter");

    private String qualifiedSuperClassName;
    private String simpleTypeName;

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

                // get annotation values
                TypeElement typeElement = (TypeElement) element;
                AdapterAnnotation annotation = typeElement.getAnnotation(AdapterAnnotation.class);
                String adapterClassName = annotation.adapterClassName();
                int layoutId = annotation.layoutId();

                messager.printMessage(Diagnostic.Kind.WARNING, adapterClassName);
                messager.printMessage(Diagnostic.Kind.WARNING, String.valueOf(layoutId));

                TypeMirror listItemTypeTypeMirror = null;
                Class<?> listItemClass = null;
                try {
                    listItemClass = annotation.itemType();
                    qualifiedSuperClassName = listItemClass.getCanonicalName();
                    simpleTypeName = listItemClass.getSimpleName();
                } catch (MirroredTypeException mte) {
                    DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
                    listItemTypeTypeMirror = mte.getTypeMirror();
                    TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
                    qualifiedSuperClassName = classTypeElement.getQualifiedName().toString();
                    simpleTypeName = classTypeElement.getSimpleName().toString();
                }

                TypeMirror viewHolderTypeMirror = null;
                Class<?> viewHolderClass = null;
                try {
                    viewHolderClass = annotation.viewHolderClass();
                } catch (MirroredTypeException mte) {
                    viewHolderTypeMirror = mte.getTypeMirror();
                }

                messager.printMessage(Diagnostic.Kind.WARNING, qualifiedSuperClassName);
                messager.printMessage(Diagnostic.Kind.WARNING, simpleTypeName);

                // List<T>
                TypeName parameterizedItemListTypeName = ParameterizedTypeName.get(list,
                        listItemClass == null ? ClassName.get(listItemTypeTypeMirror) : ClassName.get(listItemClass));

                // RecyclerView.Adapter<ViewHolder>
                TypeName adapterParametrized = ParameterizedTypeName.get(classAdapter,
                        viewHolderClass == null ? ClassName.get(viewHolderTypeMirror) : ClassName.get(viewHolderClass));

                // Class
                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(adapterClassName)
                        .addModifiers(Modifier.PUBLIC)
                        .superclass(adapterParametrized);

                // Constructor1
                MethodSpec.Builder constructor1 = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(parameterizedItemListTypeName, "list")
                        .addStatement("itemList = $L", "list");
                classBuilder.addMethod(constructor1.build());

                // Constructor2
                MethodSpec.Builder constructor2 = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .addParameter(parameterizedItemListTypeName, "list")
                        .addParameter(classContext, "context")
                        .addStatement("$T rc = new $T($L)", recyclerViewClass, recyclerViewClass, "context")
                        .addStatement("itemList = $L", "list");
                classBuilder.addMethod(constructor2.build());

                // Field List
                FieldSpec missingFeatures = FieldSpec.builder(parameterizedItemListTypeName, "itemList")
                        .addModifiers(Modifier.PRIVATE)
                        .initializer("new $T<>()", arrayList)
                        .build();
                classBuilder.addField(missingFeatures);


                // ClickListener Field
                FieldSpec builderItemClick = FieldSpec.builder(itemClickClass, "itemClickListener")
                        .addModifiers(Modifier.PRIVATE)
                        .build();
                classBuilder.addField(builderItemClick);

                // LongClickListener Field
                FieldSpec builderLongItemClick = FieldSpec.builder(itemLongClass, "itemLongClickListener")
                        .addModifiers(Modifier.PRIVATE)
                        .build();
                classBuilder.addField(builderLongItemClick);

                // public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
                MethodSpec createViewHolder = MethodSpec.methodBuilder("onCreateViewHolder")
                        .addAnnotation(classOverrideAnnotation)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(viewHolderClass == null ? ClassName.get(viewHolderTypeMirror) : ClassName.get(viewHolderClass))
                        .addParameter(classViewGroup, "parent")
                        .addParameter(TypeName.INT, "viewType")
                        .addStatement("$T inflator = $T.from(parent.getContext())", classLayoutInflator, classLayoutInflator)
                        .addStatement("$T view = $L.inflate($L, $L, false)", classView, "inflator", layoutId, "parent")
                        .addStatement("return new $T($L)", viewHolderClass == null ? ClassName.get(viewHolderTypeMirror) : ClassName.get(viewHolderClass), "view")
                        .build();
                classBuilder.addMethod(createViewHolder);

                // public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
                MethodSpec bindViewHolder = MethodSpec.methodBuilder("onBindViewHolder")
                        .addAnnotation(classOverrideAnnotation)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(viewHolderClass == null ? ClassName.get(viewHolderTypeMirror) : ClassName.get(viewHolderClass), "holder")
                        .addParameter(TypeName.INT, "position")
                        .addStatement("$L.bind($L.get($L), $L)", "holder", "itemList", "position", "position")
                        .build();
                classBuilder.addMethod(bindViewHolder);

                // public int getItemCount() {
                MethodSpec getItemCount = MethodSpec.methodBuilder("getItemCount")
                        .addAnnotation(classOverrideAnnotation)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.INT)
                        .addStatement("return $L.size()", "itemList")
                        .build();
                classBuilder.addMethod(getItemCount);

                // Builder Class
                TypeSpec.Builder innerClassBuilder = TypeSpec.classBuilder("Builder")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

                // Builder Constructor
                MethodSpec.Builder innerClassConstructor = MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(parameterizedItemListTypeName, "itemList")
                        .addStatement("this.itemList = itemList");
                innerClassBuilder.addMethod(innerClassConstructor.build());

                // Builder List Field
                FieldSpec builderListField = FieldSpec.builder(parameterizedItemListTypeName, "itemList")
                        .addModifiers(Modifier.PRIVATE)
                        .initializer("new $T<>()", arrayList)
                        .build();
                innerClassBuilder.addField(builderListField);


                // Builder ClickListener Field
                /*FieldSpec builderItemClick = FieldSpec.builder(itemClickClass, "itemClickListener")
                        .addModifiers(Modifier.PRIVATE)
                        .build();*/
                innerClassBuilder.addField(builderItemClick);


                // Builder LongClickListener Field
                /*FieldSpec builderLongItemClick = FieldSpec.builder(itemClickClass, "itemLongClickListener")
                        .addModifiers(Modifier.PRIVATE)
                        .build();*/
                innerClassBuilder.addField(builderLongItemClick);


                // Builder Class Type
                String className = adapterClassName + "." + "Builder";
                ClassName builderClassType = ClassName.get("ir.mohsenafshar.adapters", className);


                // ClickListener
                MethodSpec setItemClickListener = MethodSpec.methodBuilder("setItemClickListener")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(itemClickClass, "itemClickListener")
                        .returns(builderClassType)
                        .addStatement("this.itemClickListener = itemClickListener")
                        .addStatement("return this").build();
                innerClassBuilder.addMethod(setItemClickListener);


                // LongClickListener
                MethodSpec setItemLongClickListener = MethodSpec.methodBuilder("setItemLongClickListener")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(itemLongClass, "itemLongClickListener")
                        .returns(builderClassType)
                        .addStatement("this.itemLongClickListener = itemLongClickListener")
                        .addStatement("return this").build();
                innerClassBuilder.addMethod(setItemLongClickListener);


                // Adapter Class Type
                ClassName adapterClassType = ClassName.get("ir.mohsenafshar.adapters", adapterClassName);

                MethodSpec build = MethodSpec.methodBuilder("build")
                        .addModifiers(Modifier.PUBLIC)
                        .returns(adapterClassType)
                        .addStatement("return new $T($L)", adapterClassType, "itemList").build();
                innerClassBuilder.addMethod(build);








                classBuilder.addType(innerClassBuilder.build());


                JavaFile.builder("ir.mohsenafshar.adapters", classBuilder.build()).build().writeTo(filer);
            }


            /*
             * 3- Write generated class to a file
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
