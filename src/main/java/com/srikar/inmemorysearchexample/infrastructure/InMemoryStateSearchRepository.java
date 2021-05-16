package com.srikar.inmemorysearchexample.infrastructure;

import com.srikar.inmemorysearchexample.domain.StateSearchRepository;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.jooq.lambda.Seq.seq;

@Repository
public class InMemoryStateSearchRepository implements StateSearchRepository {

    private static final List<String> INDIAN_STATES = List.of(
            "Andhra Pradesh",
            "Arunachal Pradesh",
            "Assam",
            "Bihar",
            "Chhattisgarh",
            "Goa",
            "Gujarat",
            "Haryana",
            "Himachal Pradesh",
            "Jammu and Kashmir",
            "Jharkhand",
            "Karnataka",
            "Kerala",
            "Madhya Pradesh",
            "Maharashtra",
            "Manipur",
            "Meghalaya",
            "Mizoram",
            "Nagaland",
            "Odisha",
            "Punjab",
            "Rajasthan",
            "Sikkim",
            "Tamil Nadu",
            "Telangana",
            "Tripura",
            "Uttarakhand",
            "Uttar Pradesh",
            "West Bengal",
            "Andaman and Nicobar Islands",
            "Chandigarh",
            "Dadra and Nagar Haveli",
            "Daman and Diu",
            "Delhi",
            "Lakshadweep",
            "Puducherry"
    );

    @Override
    public List<String> search(String inputState) {
        return
                seq(FuzzySearch.extractTop(inputState, INDIAN_STATES, 5))
                        .map(ExtractedResult::getString)
                        .toList();
    }
}
