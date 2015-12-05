package edu.cmu.cs.varex.assertion;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * AOP based runtime monitor for NonNull declarations
 *
 * see https://github.com/vy/null-check
 */


@Aspect
public class NullMonitor {

  private static class MethodArgument {

    private final int index;
    private final String name;
    private final List<Annotation> annotations;
    private final Object value;

    private MethodArgument(int index, String name, List<Annotation> annotations, Object value) {
      this.index = index;
      this.name = name;
      this.annotations = Collections.unmodifiableList(annotations);
      this.value = value;
    }

    public int getIndex() { return index; }

    public String getName() { return name; }

    public List<Annotation> getAnnotations() { return annotations; }

    public boolean hasAnnotation(Class<? extends Annotation> type) {
      for (Annotation annotation : annotations)
        if (annotation.annotationType().equals(type))
          return true;
      return false;
    }

    public Object getValue() { return value; }

    public static List<MethodArgument> of(JoinPoint joinPoint) {
      List<MethodArgument> arguments = new ArrayList<>();
      CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
      String[] names = codeSignature.getParameterNames();
      Annotation[][] annotations;
      if (joinPoint.getStaticPart().getSignature() instanceof MethodSignature) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getStaticPart().getSignature();
        annotations = methodSignature.getMethod().getParameterAnnotations();
      } else {
        ConstructorSignature methodSignature = (ConstructorSignature) joinPoint.getStaticPart().getSignature();
        annotations = methodSignature.getConstructor().getParameterAnnotations();
      }
      Object[] values = joinPoint.getArgs();
      for (int i = 0; i < values.length; i++)
        arguments.add(new MethodArgument(i, names[i], Arrays.asList(annotations[i]), values[i]));
      return Collections.unmodifiableList(arguments);
    }

  }

  Class<? extends Annotation> nonNull = Nonnull.class;

  @Before("execution(* *(.., @javax.annotation.Nonnull (*), ..))")
  public void nullCheckParameter(JoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    for (MethodArgument argument : MethodArgument.of(joinPoint))
      if (argument.hasAnnotation(Nonnull.class) && argument.getValue() == null)
        throw new NullPointerException(String.format("%s: argument \"%s\" (at position %d) cannot be null",
                methodSignature.getMethod(), argument.getName(), argument.getIndex()));
  }

  @Before("execution(new(.., @javax.annotation.Nonnull (*), ..))")
  public void nullCheckConstructorParameter(JoinPoint joinPoint) {
    ConstructorSignature methodSignature = (ConstructorSignature) joinPoint.getSignature();
    for (MethodArgument argument : MethodArgument.of(joinPoint))
      if (argument.hasAnnotation(Nonnull.class) && argument.getValue() == null)
        throw new NullPointerException(String.format("%s: argument \"%s\" (at position %d) cannot be null",
                methodSignature.getConstructor(), argument.getName(), argument.getIndex()));
  }

  @Before("set(@javax.annotation.Nonnull * *.*)")
  public void nullCheckFieldAssignment(JoinPoint joinPoint) {
    FieldSignature sig = (FieldSignature) joinPoint.getStaticPart().getSignature();
//    boolean isNonnull = false;
//    for (Annotation a : sig.getField().getAnnotations())
//      if (a.annotationType().equals(nonNull))
//        isNonnull = true;
//    if (isNonnull) {
    Object value = joinPoint.getArgs()[0];
    if (value == null)
      throw new NullPointerException(String.format("%s: field cannot be assigned with null",
              sig.getField()));
//    }
  }

  @AfterReturning(pointcut = "execution(@javax.annotation.Nonnull * *(..))", returning = "result")
  public void nullCheckReturn(JoinPoint joinPoint, Object result) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getStaticPart().getSignature();
    if (result == null)
      throw new NullPointerException(String.format("%s: cannot return null",
              methodSignature.getMethod()));
  }


}