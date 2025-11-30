package com.raven.swing.table;

import com.raven.model.ModelTransaction;

public interface EventAction {
    public void delete(ModelTransaction tx);
    public void update(ModelTransaction tx);
}
