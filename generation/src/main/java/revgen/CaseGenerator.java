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
    }

    public Case generateOne() {
        int sex = sex_gen.sample();
        int implant = implant_gen[sex].sample();
        float ttr = (float) ttr_gen[sex][implant].sample();
        
        return new Case(sex, implant, ttr);
    }

    public List<Case> generate() {
        List<Case> cases = new ArrayList<Case>();
        for (int i = 0; i < params.numCases; i++) {
            cases.add(generateOne());
        }
        return cases;
    } 
}
