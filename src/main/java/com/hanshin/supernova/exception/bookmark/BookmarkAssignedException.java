package com.hanshin.supernova.exception.bookmark;

import com.hanshin.supernova.exception.BusinessException;
import com.hanshin.supernova.exception.dto.ErrorType;

public class BookmarkAssignedException extends BusinessException {
    public BookmarkAssignedException(ErrorType errorType) {
      super(errorType);
    }
}