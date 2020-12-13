package com.vortalmc.chat.utils.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The EventHandler annotation.
 * 
 * <p>
 * Put this annotation above methods that will handle custom VortalMC-Chat
 * events.
 * </p>
 * 
 * <p>
 * Note: The class containing the method(s) that will handle events must
 * implement {@link com.vortalmc.chat.utils.event.Listener Listener}, and must
 * be registered with the {@link com.vortalmc.chat.VortalMCChat VortalMC-Chat}
 * plugin using the
 * {@link com.vortalmc.chat.VortalMCChat#getInternalEventManager()
 * InternalEventManager} for the event to be handled.
 * </p>
 * 
 * @author Myles Deslippe
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

}
