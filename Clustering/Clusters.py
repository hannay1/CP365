import numpy as np
np.random.seed(42)


class sparseVector:

    def __init__(self):
        rawData = np.genfromtxt('u.data')
        self.mur = (rawData[:, 1], rawData[:, 0], rawData[:, 2])
        self.dDict = self.getDoubleDict() #holds {movieID : {userID, rating}}

    def getDoubleDict(self):
        doubleDict = {}
        i = 0
        for film in self.mur[0]:
            if film not in doubleDict:
                doubleDict[film] = dict([(self.mur[1][i], self.mur[2][i])])
            else:
                self.addNewRating(doubleDict, film, self.mur[1][i], self.mur[2][i])
            i += 1
        return doubleDict

    def addNewRating(self, dictionary, film, userID, rating):
        temp = dictionary[film]
        temp[userID] = rating
        dictionary[userID] = temp

class KM:

    def __init__(self):
        self.k = 5
        self.centroids = []
        self.points = []

    def randomCluster(self, sv):
        i = 0
        while i <= self.k:
            i+=1
            newUsrs = []
            rand = np.random.uniform(1,5, len(sv.mur[1]))
            self.centroids.append(rand)
            self.points.append(newUsrs)


    def generateCluster(self, sv):
        #make random cluster for each k
        #for each film, for each centroid, calculate distance from each user to centroid
        #calculate new distance after finding minimum point distance
        self.randomCluster(sv)
        for film in sv.dDict.keys():
            filmDiff = []
            filmList = []
            newCentroid = np.zeros(len(sv.mur[1]))
            for centroid in self.centroids:
                totalDistance = 0
                for userID in sv.dDict[film].keys():
                    totalDistance += abs(centroid[int(userID)] - sv.dDict[film][userID])
                filmDiff.append(totalDistance)
            nearest_centroid = np.argmin(filmDiff)
            filmList.append(film)
            self.points[nearest_centroid].append(filmList)
            for i in range(len(self.points[nearest_centroid])):
                for j in range(len(self.points[nearest_centroid][i])):
                    for user in sv.dDict[self.points[nearest_centroid][i][j]].keys():
                        newCentroid[int(user)] += sv.dDict[self.points[nearest_centroid][i][j]][user]
            newCentroid = np.round(newCentroid / len(self.points[nearest_centroid]))
            self.centroids[nearest_centroid] = newCentroid



def main():
    print("clustering...")
    sv = sparseVector()
    kay = KM()
    kay.generateCluster(sv)
    for i in range(len(kay.points)):
        print("Group:", i + 1)
        for j in range(10):
            print(kay.points[i][j])

main()