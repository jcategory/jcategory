package org.jgum.strategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A processing object which task is defined by a given method and its arguments invoked reflectively on a given object.
 * @author sergioc
 *
 */
public class ReflectiveProcessingObject implements ProcessingObject {
	
	/**
	 * 
	 * @param objects a list of objects.
	 * @param command the method to invoke on this list of objects.
	 * @param args the arguments of the method.
	 * @return a list of (reflective) processing objects.
	 */
	public static List<ProcessingObject> asProcessingObjects(List<? extends Object> objects, Method command, Object args[]) {
		List<ProcessingObject> processingObjects = new ArrayList<>();
		for(Object o : objects) {
			processingObjects.add(new ReflectiveProcessingObject(o, command, args));
		}
		return processingObjects;
	}
	
	private final Object object;
	private final Method command;
	private final Object args[];
	
	/**
	 * 
	 * @param object the object target of a reflective method invocation.
	 * @param command the reflective method to invoke.
	 * @param args the arguments of the reflective method.
	 */
	public ReflectiveProcessingObject(Object object, Method command, Object args[]) {
		this.object = object;
		this.command = command;
		this.args = args;
	}
	
	@Override
	public Object apply() {
		try {
			return command.invoke(object, args);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch(InvocationTargetException e) {
			if((e.getTargetException() instanceof RuntimeException))
				throw ((RuntimeException)e.getTargetException());
			else
				throw new RuntimeException(e.getTargetException());
		}
	}
}