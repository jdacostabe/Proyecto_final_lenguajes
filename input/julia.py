def example(a,b,c):
    c = a+b
    a = a
    return c

a = example(1,2,3)
b = example(4,5,6)
print(a)
print(b)

