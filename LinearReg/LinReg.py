import numpy as np
np.random.seed(42)

class linearRegression():

    def __init__(self, a, c, temp):
        self.weight = np.random.rand(1)
        self.bias = np.random.rand(1)
        self.alpha = a #learning rate
        self.converg = c #sets when to stop recursing (convergence reached)
        self.data = np.genfromtxt("djia_temp.csv", delimiter=';', skip_header=1)
        self.iv = self.data[:, temp] # 2 for high temp, 3 for avg
        self.dv = self.data[:, 1] # DJIA numbers

    def linReg(self, start, end):
        if abs(start - end) > self.converg:
            start = np.sum(np.power((self.iv*self.weight+self.bias), 2))
            err = (self.iv*self.weight+self.bias) - self.dv
            self.weight -= np.sum(self.alpha * err * self.dv / len(self.dv))
            self.bias -= np.sum(self.alpha * err * 1.0 / len(self.dv))
            end = np.sum(np.power((self.iv*self.weight+self.bias), 2))
            self.linReg(start, end)
        else:
            print("final weight", ":", self.weight)
            print("final bias", ":", self.bias)
            print("final cost:", ":", end)
            return

    def go(self):
        start = np.sum(np.power((self.iv*self.weight+self.bias), 2))
        self.linReg(start, 0)

def main():
    print("----------HIGH TEMP----------")
    lr1 = linearRegression(0.000001, 0.01, 2)
    lr1.go()
    print("----------AVG TEMP----------")
    lr2 = linearRegression(0.000001, 0.01, 3)
    lr2.go()

main()