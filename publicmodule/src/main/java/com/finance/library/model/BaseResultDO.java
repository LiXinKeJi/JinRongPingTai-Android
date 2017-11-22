package com.finance.library.model;

/**
 * User : yh
 * Date : 17/8/14
 */

public class BaseResultDO {
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    private String result;
    private String resultNote;
}
