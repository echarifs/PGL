
import matplotlib.pyplot as plt
import numpy as np
from numpy import random as rd
import time

PI = 3.141592653589793238462643383279502884197169

def fact(n):
    if n==0:
        return 1
    elif n == 1:
        return 1
    elif n == 2:
        return 2
    elif n == 3:
        return 6
    elif n == 4:
        return 24
    elif n == 5:
        return 120
    elif n == 6:
    	return 720
    else:
        return n*fact(n-1)
#LOOKUP TABLE Table[0] = Vn  Table[1] = sin(Vn) Table[2] = cos(Vn)
Table = [np.linspace(1,PI,10**4),np.sin(np.linspace(1,PI,10**4)),np.cos(np.linspace(1,PI,10**4))]

def cos_t(x,ordre):
    start = time.time()
    sum = 0
    if (x<10**(-5)):
    	return 1-(x**2)/2,time.time()-start
    
    elif (x<1):
        for i in range(ordre+1):
            sum += ((-1)**i) * x**(2*i)/fact(2*i)
        return sum, time.time()-start
    # elif (x - Table[0][int((x-1)*(10**4 -1)/(PI-1))] == 0):
    # 	return Table[2][int((x-1)*(10**4 -1)/(PI-1))],time.time()-start
        

def sin_t(x,ordre):
    start = time.time()
    sum = 0
    if (x<10**(-5)):
    	return x, time.time()-start
    elif (x<1):
        for i in range(ordre+1):
            sum += ((-1)**i) * x**(2*i +1)/fact(2*i+1)
        return sum, time.time()-start
    elif (x<=PI/2):
        n = int((x-1)*((10**4)-1)/(PI-1))
        epsilon = x - Table[0][n]
        c_value, c_time = cos_t(epsilon,ordre)
        s_value, s_time = sin_t(epsilon,ordre)
        return Table[1][n]*c_value+Table[2][n]*s_value, c_time + s_time
    elif (x<=PI):
    	return sin_t(PI-x,ordre)




# #ordre 5
# inter = np.linspace(0,0.999999999999,100000)
# taylor = np.array([sin_t(x,5) for x in inter ])
# sine = np.sin(inter)

# f = open("simo.txt","w")
# f.write("taylor - sin | time")
# mean,w9t = 0,0
# for e in range(100000):
#     f.write(str(taylor[e][0]-sine[e])+"  ")
#     f.write(str(taylor[e][1]))
#     f.write("\n")
#     mean += taylor[e][0]-sine[e]
#     w9t += taylor[e][1]
# print(inter[99999],taylor[99999][0]-sine[99999])
# f.write(str(mean/100000) +" " + str(w9t/100000))
# f.close()

#ordre 6
#NOT RANDOM
# inter = np.linspace(0,3,10000000)
# taylor = np.array([sin_t(x,6) for x in inter ])
# sine = np.sin(inter)

# f = open("FirstAlg.txt","w")
# f.write("taylor - sin | time")
# mean,w9t = 0,0
# for e in range(1000000):
#     f.write(str(taylor[e][0]-sine[e])+"  ")
#     f.write(str(taylor[e][1]))
#     f.write("\n")
#     mean += taylor[e][0]-sine[e]
#     w9t += taylor[e][1]
# print(str(mean/10000000) +" " + str(w9t/10000000))
# print("size = ", 3*Table[1].size*Table[1].itemsize)
# f.write(str(mean/10000000) +" " + str(w9t/10000000))
# f.close()

#RANDOM
inter2 = rd.uniform(0,3,10000000)
taylor = np.array([sin_t(x,6) for x in inter2 ])
sine = np.sin(inter2)

f = open("FirstAlg-RANDOM.txt","w")
f.write("x |  taylor | sin | taylor - sin | time   \n")
mean,w9t = 0,0
for e in range(1000000):
    f.write(str(inter2[e])+ "  ")
    f.write(str(taylor[e][0])+"  ")
    f.write(str(sine[e])+ "  ")
    f.write(str(taylor[e][0]-sine[e])+"  ")
    f.write(str(taylor[e][1]))
    f.write("\n")
    mean += taylor[e][0]-sine[e]
    w9t += taylor[e][1]
print("RANDOM")
print(str(mean/10000000) +" " + str(w9t/10000000))
print("size = ", 3*Table[1].size*Table[1].itemsize)
f.write(str(mean/10000000) +" " + str(w9t/10000000))
f.close()



