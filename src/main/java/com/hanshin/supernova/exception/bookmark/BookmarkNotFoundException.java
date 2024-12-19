package com.hanshin.supernova.exception.bookmark;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class BookmarkNotFoundException extends BusinessException {
    public BookmarkNotFoundException(ErrorType errorType) {
      super(errorType);
    }
}