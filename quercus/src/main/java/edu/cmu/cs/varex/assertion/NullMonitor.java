package edu.cmu.cs.varex.assertion;

import edu.cmu.cs.varex.V;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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
    private final Class type;

    private MethodArgument(int index, String name, Class type, List<Annotation> annotations, Object value) {
      this.index = index;
      this.name = name;
      this.annotations = Collections.unmodifiableList(annotations);
      this.value = value;
      this.type = type;
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
      Class[] paramType = codeSignature.getParameterTypes();
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
        arguments.add(new MethodArgument(i, names[i], paramType[i], Arrays.asList(annotations[i]), values[i]));
      return Collections.unmodifiableList(arguments);
    }

  }

  Class<? extends Annotation> nonNull = Nonnull.class;
  Class<? extends Annotation> nullable = Nullable.class;

  private boolean ensureNonNull(MethodArgument argument){
    return argument.hasAnnotation(nonNull) || (V.class.isAssignableFrom(argument.type) && !argument.hasAnnotation(nullable));
  }

  @Before("execution(* *(.., @javax.annotation.Nonnull (*), ..)) || execution(* *(.., edu.cmu.cs.varex.V, ..))")
  public void nullCheckParameter(JoinPoint joinPoint) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    for (MethodArgument argument : MethodArgument.of(joinPoint))
      if (ensureNonNull(argument) && argument.getValue() == null)
        throw new NullPointerException(String.format("%s: argument \"%s\" (at position %d) cannot be null",
                methodSignature.getMethod(), argument.getName(), argument.getIndex()));
  }

  @Before("execution(new(.., @javax.annotation.Nonnull (*), ..)) || execution(new(.., edu.cmu.cs.varex.V, ..))")
  public void nullCheckConstructorParameter(JoinPoint joinPoint) {
    ConstructorSignature methodSignature = (ConstructorSignature) joinPoint.getSignature();
    for (MethodArgument argument : MethodArgument.of(joinPoint))
      if (ensureNonNull(argument) && argument.getValue() == null)
        throw new NullPointerException(String.format("%s: argument \"%s\" (at position %d) cannot be null",
                methodSignature.getConstructor(), argument.getName(), argument.getIndex()));
  }

  @Before("set(@javax.annotation.Nonnull * *.*) || (set(edu.cmu.cs.varex.V *.*) && !set(@javax.annotation.Nullable * *.*))")
  public void nullCheckFieldAssignment(JoinPoint joinPoint) {
    FieldSignature sig = (FieldSignature) joinPoint.getStaticPart().getSignature();
    Object value = joinPoint.getArgs()[0];
    if (value == null)
      throw new NullPointerException(String.format("%s: field cannot be assigned with null",
              sig.getField()));
//    }
  }

  @AfterReturning(pointcut = "execution(@javax.annotation.Nonnull * *(..)) || execution(!@javax.annotation.Nullable edu.cmu.cs.varex.V *(..))", returning = "result")
  public void nullCheckReturn(JoinPoint joinPoint, Object result) {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getStaticPart().getSignature();
    if (result == null)
      throw new NullPointerException(String.format("%s: cannot return null",
              methodSignature.getMethod()));
  }


}