package org.jgum.strategy;


/**
 * <p>
 * Class implementing the chain of responsibility pattern.
 * The class is initialized with an iterable of objects (the processing objects) in charge of managing a desired command.
 * </p>
 * 
 * @author sergioc
 *
 * @param <T> the type of objects in the chain of responsibility.
 */
public abstract class ChainOfResponsibility<T> {

	private final Iterable<T> responsibilityChain;
	
	/**
	 * 
	 * @param responsibilityChain the processing objects.
	 */
	public ChainOfResponsibility(Iterable<T> responsibilityChain) {
		this.responsibilityChain = responsibilityChain;
	}

	/**
	 * This method executes a command on each member of the responsibility chain until it finds one that can manage it.
	 * How this command execution is accomplished is implementation dependent.
	 * If a processing object throws a {@link NoMyResponsibilityException} when attempting to execute the command, the operation will be delegated to the next object in the responsibility chain.
	 * Any other exception is propagated to the caller.
	 * <p>
	 * If no object is able to process the command after exhausting the responsibility chain, a {@link NoMyResponsibilityException} is thrown.
	 * </p>
	 * @return the result of executing the command on the first object in the responsibility chain that does not throw a {@link NoMyResponsibilityException} exception.
	 */
	public Object apply() {
		for(T object : responsibilityChain) {
			try {
				return delegate(object);
			} catch (NoMyResponsibilityException e) {
			}
		}
		throw new NoMyResponsibilityException();
	}
	
	/**
	 * Subclasses should override this method to specify how a object in the responsibility chain executes the desired command.
	 * @param processingObject a processing object in the responsibility chain.
	 * @return the result of executing the command.
	 */
	protected abstract Object delegate(T processingObject);
	
}
