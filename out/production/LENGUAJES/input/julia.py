def horas_trabajadas(temprano, medio_dia, tarde, noche):
    primeras_horas=temprano+medio_dia
    ultimas_horas=tarde+noche

    noche=0
    total=temprano+medio_dia+tarde

    print(primeras_horas)
    print(ultimas_horas)
    return total

a = 2
b = 5

horas_trabajadas(4, a, a,20)
horas_trabajadas(a, 1, 5,0)




