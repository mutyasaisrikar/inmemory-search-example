package com.srikar.inmemorysearchexample.infrastructure.lucene.configuration;

import com.srikar.inmemorysearchexample.domain.State;
import com.srikar.inmemorysearchexample.infrastructure.InMemoryStateSearchRepository;
import com.srikar.inmemorysearchexample.infrastructure.lucene.LuceneStateSearchRepository;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

import static com.srikar.inmemorysearchexample.domain.State.state;

@Configuration
public class LuceneConfiguration {

    private static final List<State> INDIAN_STATES = List.of(
        state("AP", "Andhra Pradesh"),
        state("AR", "Arunachal Pradesh"),
        state("AS", "Assam"),
        state("BR", "Bihar"),
        state("CG", "Chhattisgarh"),
        state("GA", "Goa"),
        state("GJ", "Gujarat"),
        state("HR", "Haryana"),
        state("HP", "Himachal Pradesh"),
        state("JK", "Jammu and Kashmir"),
        state("JH", "Jharkhand"),
        state("KA", "Karnataka"),
        state("KL", "Kerala"),
        state("MP", "Madhya Pradesh"),
        state("MH", "Maharashtra"),
        state("MN", "Manipur"),
        state("ML", "Meghalaya"),
        state("MZ", "Mizoram"),
        state("NL", "Nagaland"),
        state("OR", "Orissa"),
        state("PB", "Punjab"),
        state("RJ", "Rajasthan"),
        state("SK", "Sikkim"),
        state("TN", "Tamil Nadu"),
        state("TR", "Tripura"),
        state("UK", "Uttarakhand"),
        state("UP", "Uttar Pradesh"),
        state("WB", "West Bengal"),
        state("TN", "Tamil Nadu"),
        state("TR", "Tripura"),
        state("AN", "Andaman and Nicobar Islands"),
        state("CH", "Chandigarh"),
        state("DH", "Dadra and Nagar Haveli"),
        state("DD", "Daman and Diu"),
        state("DL", "Delhi"),
        state("LD", "Lakshadweep"),
        state("PY", "Pondicherry")
    );

    private final LuceneStateSearchRepository luceneStateSearchRepository;

    public LuceneConfiguration(LuceneStateSearchRepository luceneStateSearchRepository) {
        this.luceneStateSearchRepository = luceneStateSearchRepository;
        this.luceneStateSearchRepository.save(INDIAN_STATES);
    }

    @SneakyThrows
    public static void main(String[] args) {
        LuceneStateSearchRepository luceneStateSearchRepository = new LuceneStateSearchRepository(5);
        LuceneConfiguration luceneConfiguration = new LuceneConfiguration(luceneStateSearchRepository);

        InMemoryStateSearchRepository inMemoryStateSearchRepository = new InMemoryStateSearchRepository();

        List.of("Andhra", "Andhra Pradesh", "Deli", "AP", "MP")
            .forEach(eachState -> {
                try {
                    System.out.println(eachState);
                    System.out.println(" ---lucene--- " + luceneStateSearchRepository.search(eachState));
                    System.out.println(" ---memory--- " + inMemoryStateSearchRepository.search(eachState));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }
}
