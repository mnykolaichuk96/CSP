package mnykolaichuk.prz.pracaDyplomowa.validation;

import java.io.Serializable;

public class CompositeKey implements Serializable {
    private Integer customerId;
    private Integer carId;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
