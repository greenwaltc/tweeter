package edu.byu.cs.tweeter.model.dto;

import java.util.ArrayList;
import java.util.List;

public class UpdateFeedsDTO {
    List<PostStatusDTO> batch;

    public UpdateFeedsDTO(){
        batch = new ArrayList<>();
    }

    public void add(PostStatusDTO ob) {
        batch.add(ob);
    }

    public int size() {
        return batch.size();
    }

    public PostStatusDTO get(int i) {
        return batch.get(i);
    }

    public void reset() {
        batch = new ArrayList<>();
    }

    public List<PostStatusDTO> getBatch() {
        return batch;
    }
}
