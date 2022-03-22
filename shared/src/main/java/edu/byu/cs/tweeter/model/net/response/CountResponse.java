package edu.byu.cs.tweeter.model.net.response;

public class CountResponse extends Response{
    private int count;

    private CountResponse(){}

    /**
     * Failure constructor.
     * @param success
     * @param message
     */
    public CountResponse(boolean success, String message) {
        super(success, message);
    }

    /**
     * Success constructor.
     * @param success
     * @param count
     */
    public CountResponse(boolean success, int count) {
        super(success);
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
