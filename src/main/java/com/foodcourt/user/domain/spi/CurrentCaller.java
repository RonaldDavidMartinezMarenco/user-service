package com.foodcourt.user.domain.spi;

import com.foodcourt.user.domain.model.Caller;

/** Reads the authenticated caller. The domain does not import a security framework. */
public interface CurrentCaller {

    Caller require();
}
