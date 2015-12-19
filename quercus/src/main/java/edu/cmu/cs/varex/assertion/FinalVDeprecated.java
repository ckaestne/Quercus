package edu.cmu.cs.varex.assertion;

import edu.cmu.cs.varex.V;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.DeclareError;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * AOP construct to ensure that all @VDeprecated methods
 * are declared final
 */
@Aspect
public class FinalVDeprecated {

	@DeclareError("execution(!final !static * *(..)) && execution(@edu.cmu.cs.varex.annotation.VDeprecated * *(..))")
	static final String msg = "All @VDeprecated methods must be final";

}