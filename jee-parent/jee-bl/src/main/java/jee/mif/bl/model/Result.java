package jee.mif.bl.model;

import java.util.function.Function;

/**
 * Created by Tadas.
 */
public class Result<T> {

    private T result;
    private ActionEnum state;
    private String additionalInfo;

    public Result(ActionEnum state) {
        this.state = state;
    }

    public Result(T result, ActionEnum state) {
        this.result = result;
        this.state = state;
    }

    public Result(ActionEnum state, String additionalInfo) {
        this.state = state;
        this.additionalInfo = additionalInfo;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public ActionEnum getState() {
        return state;
    }

    public void setState(ActionEnum state) {
        this.state = state;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public boolean isSuccess() {
        return ActionEnum.SUCCESS == state;
    }

    public <R> Result<R> map(Function<T, R> mapper) {
        if (isSuccess()) {
            return new Result<>(mapper.apply(getResult()), getState());
        } else {
            return new Result<>(getState(), getAdditionalInfo());
        }
    }
}
