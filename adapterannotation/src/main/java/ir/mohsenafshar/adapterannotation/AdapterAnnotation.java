package ir.mohsenafshar.adapterannotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AdapterAnnotation {

    String adapterClassName() default "Adapter";

    Class itemType();

//    Class viewHolderClass() default ;

    int layoutId() default 0;
}
