package revgen;

import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import java.util.List;
import java.util.ArrayList;
import revgen.Case;
import revgen.CaseGenerator;

public class CaseGenerator {
    ParameterSet params;
    MersenneTwister mt;
    BinomialDistribution sex_gen;
    BinomialDistribution[] implant_gen;
    WeibullDistribution[][] ttr_gen;
    UniformRealDistribution ttc_gen;

    public CaseGenerator(ParameterSet cparams) {
        params = cparams;

        mt = new MersenneTwister(Long.hashCode(System.currentTimeMillis()));

        sex_gen = new BinomialDistribution(mt, 1, params.theta_s);

        implant_gen = new BinomialDistribution[] {new BinomialDistribution(mt, 1, params.theta_i[0]),
                       new BinomialDistribution(mt, 1, params.theta_i[1])};

        ttr_gen = new WeibullDistribution[][] { {new WeibullDistribution(mt, params.alpha[0][0], params.beta[0][0]),
                        new WeibullDistribution(mt, params.alpha[0][1], params.beta[0][1]) },
                    {new WeibullDistribution(mt, params.alpha[1][0], params.beta[1][0]),
                        new WeibullDistribution(mt, params.alpha[1][1], params.beta[1][1]) }};

        ttc_gen = new UniformRealDistribution(mt, 0, params.study_length);
    }

    public Case generate() {
        int sex = sex_gen.sample();
        int implant = implant_gen[sex].sample();
        double ttr = ttr_gen[sex][implant].sample();
        double ttc = ttc_gen.sample();
        
        return new Case(sex, implant, ttr, ttc);
    }

    public List<Case> generate(int n) {
        List<Case> cases = new ArrayList<Case>();
        for (int i = 0; i < n; i++) {
            cases.add(generate());
        }
        return cases;
    } 
}
