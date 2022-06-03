package com.swotitup.secops;

import java.util.Set;

public class CustomerUnSupportedFieldPatchException extends RuntimeException {

    public CustomerUnSupportedFieldPatchException(Set<String> keys) {
        super("Field " + keys.toString() + " update is not allow.");
    }
}
