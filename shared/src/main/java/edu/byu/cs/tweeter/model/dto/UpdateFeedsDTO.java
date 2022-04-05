package edu.byu.cs.tweeter.model.dto;

import java.util.ArrayList;
import java.util.List;

public class UpdateFeedsDTO {
    List<StatusDTO> batch;

    public UpdateFeedsDTO(){
        batch = new ArrayList<>();
    }

    public void add(StatusDTO ob) {
        batch.add(ob);
    }

    public int size() {
        return batch.size();
    }

    public StatusDTO get(int i) {
        return batch.get(i);
    }

    public void reset() {
        batch = new ArrayList<>();
    }

    public List<StatusDTO> getBatch() {
        return batch;
    }
}
