package cz.metacentrum.perun.wui.profile.manager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PerunManager {
	String value() default "";
}
