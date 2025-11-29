package com.raven.swing.table;

import com.raven.model.ModelTransaction;

public class ModelAction {

    private ModelTransaction transaction;
    private EventAction event;

    public ModelAction(ModelTransaction transaction, EventAction event) {
        this.transaction = transaction;
        this.event = event;
    }

    public ModelAction() {
    }

    public ModelTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(ModelTransaction transaction) {
        this.transaction = transaction;
    }

    public EventAction getEvent() {
        return event;
    }

    public void setEvent(EventAction event) {
        this.event = event;
    }
}
