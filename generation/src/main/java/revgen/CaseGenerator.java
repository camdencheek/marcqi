package revgen;

import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.random.MersenneTwister;
import java.util.List;
import java.util.ArrayList;
import revgen.Case;
import revgen.CaseGenerator;

/**
 * A generator that creates cases. 
 *
 * Allows all the random generators to only be allocated once, significantly 
 * decreasing runtime.
 */
public class CaseGenerator {

    ParameterSet params;
    MersenneTwister mt;
    BinomialDistribution sex_gen;
    BinomialDistribution[] implant_gen;
    WeibullDistribution[][] ttr_gen;

    // Constructor
    public CaseGenerator(ParameterSet cparams) {
        params = cparams;

        // Create a Mersenne Twister PRNG seeded with a hashed version of the current time
        mt = new MersenneTwister(Long.hashCode(System.currentTimeMillis()));

        // Create a distribution of sex to be sampled from
        sex_gen = new BinomialDistribution(mt, 1, params.theta_s);

        // Create an array of distributions of implant type where [0] corresponds to the male 
        // and [1] corresponds to the female distribution
        implant_gen = new BinomialDistribution[] {new BinomialDistribution(mt, 1, params.theta_i[0]),
                       new BinomialDistribution(mt, 1, params.theta_i[1])};

        // Create a 2d array of distributions of TTR
        // ttr_gen[sex][implant]
        ttr_gen = new WeibullDistribution[][] { {new WeibullDistribution(mt, params.alpha[0][0], params.beta[0][0]),
                        new WeibullDistribution(mt, params.alpha[0][1], params.beta[0][1]) },
                    {new WeibullDistribution(mt, params.alpha[1][0], params.beta[1][0]),
                        new WeibullDistribution(mt, params.alpha[1][1], params.beta[1][1]) }};
    }

    // Generate a single case
    public Case generateOne() {
        int sex = sex_gen.sample();
        int implant = implant_gen[sex].sample();
        float ttr = (float) ttr_gen[sex][implant].sample();
        
        return new Case(sex, implant, ttr);
    }

    // Generate a list of cases
    public List<Case> generate() {
        List<Case> cases = new ArrayList<Case>();
        for (int i = 0; i < params.numCases; i++) {
            cases.add(generateOne());
        }
        return cases;
    } 
}
