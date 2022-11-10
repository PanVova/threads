import math
import time
from mpi4py import MPI
import numpy as np
from numpy import ndarray


def monteCarloMethod(array):
    # transpose method for array in numpy library
    # then we calculate our part of array squared random x,y and taking root
    # of it and check when the value will be lower or equal to 1
    x = pow(array.T[0], 2)
    y = pow(array.T[1], 2)
    circlePoints = np.count_nonzero((x + y) <= 1)
    squarePoints = len(array)
    pi = (circlePoints / squarePoints) * 4
    return pi


if __name__ == "__main__":
    mpiCommWorld = MPI.COMM_WORLD
    mpiSize, mpiRank = (mpiCommWorld.Get_size(), mpiCommWorld.Get_rank())
    n = 100000

    print("MPI SIZE: " + str(mpiSize))
    print("MPI RANK: " + str(mpiRank) + "\n")

    parts = math.ceil(n / mpiSize)  # divide by sizes
    arr: ndarray = np.random.rand(n, 2)  # generate numbers

    data = []
    for i in range(0, mpiSize):
        data.append(arr[0: (i + 1) * parts])

    # will scatter the data to specific channels by parts
    dots = mpiCommWorld.scatter(data, root=0)

    # calculate the time
    startTime = time.time()

    piNumber = monteCarloMethod(dots)

    endTime = time.time()

    results = mpiCommWorld.gather(piNumber, root=0)

    for result in results:
        print(result)
