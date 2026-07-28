package com.angeloni.nutricare.event;

import org.springframework.context.ApplicationEvent;

public class LocaleChangedEvent extends ApplicationEvent {
    public LocaleChangedEvent(Object source) { super(source); }
}
