package com.vortalmc.chat.utils.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * An internal event management utility.
 * 
 * <p>
 * This class is used to manage {@link com.vortalmc.chat.utils.event.Listener
 * Listeners} that handle custom VortalMC-Chat events.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class InternalEventManager {

	/**
	 * The list of registered listeners.
	 */
	private final ArrayList<Listener> listeners;

	/**
	 * Create a new internal event manager.
	 */
	public InternalEventManager() {
		listeners = new ArrayList<Listener>();
	}

	/**
	 * Register a listener with the plugin.
	 * 
	 * @param listener The listener to register.
	 */
	public void registerListener(Listener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Unregister a listener with the plguin.
	 * 
	 * @param listener The listener to unregister.
	 */
	public void unregisterListener(Listener listener) {
		if (this.containsListener(listener))
			this.listeners.remove(listener);
	}

	/**
	 * Check if a listener is registered with the plugin.
	 * 
	 * @param listener The listener to check.
	 * 
	 * @return The truth value associated with the listener being registered.
	 */
	public boolean containsListener(Listener listener) {
		return this.listeners.contains(listener);
	}

	/**
	 * Get a list of all the registered listeners.
	 * 
	 * <p>
	 * Note: This is a clone of the original list, modifications made to the
	 * elements of the list will affect the origional elements.
	 * </p>
	 * 
	 * @return The list of registered listeners.
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Listener> getListeners() {
		return (ArrayList<Listener>) this.listeners.clone();
	}

	/**
	 * Dispatch an event.
	 * 
	 * @param event The event to dispatch.
	 */
	public void dispatchEvent(Event event) {
		for (Listener index : listeners)
			this.executeEvent(index.getClass(), index, event);
	}

	/**
	 * Execute an event.
	 * 
	 * @param clazz    The listener class.
	 * @param executor The class executing the event.
	 * @param args     The event arguments.
	 */
	private void executeEvent(Class<?> clazz, Object executor, Object... args) {

		for (Method index : clazz.getDeclaredMethods()) {

			if (index.getDeclaredAnnotation(EventHandler.class) != null) {

				if (index.getParameterCount() == 0)
					throw new InvalidEventHandlerException("Error: No paramater was specified!");

				String paramaterType = index.getParameterTypes()[0].getTypeName();
				String argumentType = args[0].getClass().getTypeName();
				String eventType = Event.class.getTypeName();

				if (paramaterType.equals(argumentType) || paramaterType.equals(eventType)) {

					try {
						index.invoke(executor, args);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
