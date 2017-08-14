from scipy.stats import beta

def exact_ci(alpha, n, k):
    pub = 1 - beta.ppf(alpha / 2, n - k, k + 1)
    plb = 1 - beta.ppf(1 - alpha / 2, n - k + 1, k)

    return (plb, pub)
