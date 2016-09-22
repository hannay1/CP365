import matplotlib.pyplot as plt
import numpy as np

np.random.seed(42)

def sigmoid(x):
    return 1 / (1 + np.exp(-x))

def sigDeriv(x):
    return x * (1 - x)

class Model:
    def __init__(self, depth, learning_rate):
        self.depth = depth
        self.learning_rate = learning_rate

    def getDepth(self, inDeep):
        return inDeep < len(self.depth)

    def iteration(self, X, y):
        temp = X
        inDeep = len(self.depth) - 1
        for d in self.depth:
            temp = d.forward(temp)
            err = self.calculateDerivError(y, temp)
        self.getBack(err, inDeep)

    def getBack(self, err, inDeep):
        while inDeep >= 0:
            err = self.depth[inDeep].backward(err).T
            inDeep -= 1

    def train(self, X, y, generation):

        for i in range(generation):
            self.iteration(X, y)
            self.reportAccuracy(X,y)

    def reportAccuracy(self, X, y):
        temp = X
        for d in (self.depth):
            temp = d.forward(temp)
        temp = np.round(temp)
        c = np.count_nonzero(y - temp)
        correct = len(X) - c
        print("%.4f" % (float(correct) * 100.0 / len(X)))

    def calculateDerivError(self, y, pred):
        return 2 * (y - pred)

    def calculateError(self, y, pred):
        return (np.sum(np.power((y - pred), 2)))

class Depth:
    def __init__(self, learning_rate, p_in, p_out):
        self.weights = np.random.rand(p_in, p_out)
        self.learning_rate = learning_rate

    def forward(self, X):
        self.incoming = X
        act = X.dot(self.weights)
        act = sigmoid(act)
        self.outputs = act
        return act

    def backward(self, err):
        err *= sigDeriv(self.outputs)
        self.weights += self.incoming.T.dot(err) * self.learning_rate
        return self.weights.dot(err.T)


def loadBC(filename='breast_cancer.csv'):
    my_data = np.genfromtxt(filename, delimiter=',', skip_header=1)
    y = (my_data[:, 10] / 2) - 1
    X = my_data[:, :10]
    X_norm = X / X.max(axis=0)
    return X_norm, y

def loadWine(filename='winequality-white.csv'):
    my_data = np.genfromtxt(filename, delimiter=';', skip_header=1)
    y = ((my_data[:, 11] / 10) -1).round()
    X = my_data[:, :11]
    X_norm = X / X.max(axis=0)
    return X_norm, y

def main():
    lr = 0.01

    print("Dataset 1: ")
    X, y = loadBC()
    X = np.round(X)
    y = y.reshape(683, 1)
    depth = []
    input = Depth(lr, 10, 25)
    hidden = Depth(lr, 25, 1)
    depth.append(input)
    depth.append(hidden)
    model = Model(depth, lr)
    model.train(X, y, 1000)

    print("Dataset 2: ")
    X1, y1 = loadWine()
    X1 = np.round(X1)
    y1 = y1.reshape(4898, 1)
    depth = []
    input1 = Depth(lr, 11, 30)
    hidden1 = Depth(lr, 30, 1)
    depth.append(input1)
    depth.append(hidden1)
    md = Model(depth, lr)
    md.train(X1, y1, 10000)


main()