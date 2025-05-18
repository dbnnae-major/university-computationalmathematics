import math

def left_rectangles(f, a, b, n):
    h = (b - a) / n
    integral = 0.0
    for i in range(n):
        x = a + i * h
        integral += f(x)
    return integral * h

def right_rectangles(f, a, b, n):
    h = (b - a) / n
    integral = 0.0
    for i in range(1, n + 1):
        x = a + i * h
        integral += f(x)
    return integral * h

def integral_print(a,b):
    print(b**(1.5)*2/3 - a**(1.5)*2/3)

def middle_rectangles(f, a, b, n):
    h = (b - a) / n
    integral = 0.0
    for i in range(n):
        x = a + (i + 0.5) * h
        integral += f(x)
    return integral * h

def trapezoid(f, a, b, n):
    h = (b - a) / n
    integral = (f(a) + f(b)) / 2
    for i in range(1, n):
        x = a + i * h
        integral += f(x)
    return integral * h

def simpson(f, a, b, n):
    if n % 2 != 0:
        n += 1  # Метод Симпсона требует чётное n
    h = (b - a) / n
    integral = f(a) + f(b)
    for i in range(1, n):
        x = a + i * h
        if i % 2 == 0:
            integral += 2 * f(x)
        else:
            integral += 4 * f(x)
    return integral * h / 3

def calculate_integral():
    print("Выберите функцию для интегрирования:")
    print("1. x^2")
    print("2. sin(x)")
    print("3. e^x")
    print("4. 1/x")
    print("5. sqrt(x)")

    choice = int(input("Введите номер функции (1-5): "))

    if choice == 1:
        f = lambda x: x ** 2
        func_name = "x^2"
    elif choice == 2:
        f = lambda x: math.sin(x)
        func_name = "sin(x)"
    elif choice == 3:
        f = lambda x: math.exp(x)
        func_name = "e^x"
    elif choice == 4:
        f = lambda x: 1 / x
        func_name = "1/x"
    elif choice == 5:
        f = lambda x: math.sqrt(x)
        func_name = "sqrt(x)"
    else:
        print("Неверный выбор функции")
        return

    a = float(input("Введите нижний предел интегрирования a: "))
    b = float(input("Введите верхний предел интегрирования b: "))

    if choice == 4 and (a == 0 and b == 0):
        print("Ошибка: для функции 1/x пределы должны быть отличны от 0.")
        return
    if choice == 5 and (a < 0 or b < 0):
        print("Ошибка: для функции sqrt(x) пределы не могут быть отрицательными.")
        return
    if a >= b:
        print("Ошибка: верхний предел должен быть больше нижнего.")
        return

    epsilon = float(input("Введите требуемую точность (например, 0.001): "))

    print("\nВыберите метод интегрирования:")
    print("1. Левые прямоугольники")
    print("2. Правые прямоугольники")
    print("3. Средние прямоугольники")
    print("4. Метод трапеций")
    print("5. Метод Симпсона")

    method_choice = int(input("Введите номер метода (1-5): "))

    methods = {
        1: ("Левые прямоугольники", left_rectangles),
        2: ("Правые прямоугольники", right_rectangles),
        3: ("Средние прямоугольники", middle_rectangles),
        4: ("Метод трапеций", trapezoid),
        5: ("Метод Симпсона", simpson)
    }

    if method_choice not in methods:
        print("Неверный выбор метода")
        return

    method_name, method_func = methods[method_choice]

    print("\nРезультаты вычислений:")
    print("Функция: ", func_name)
    print("Интервал: [", a, ",", b, "]")
    print("Метод: ", method_name)
    print("Точность: ", epsilon)
    print("\nИтерационный процесс:")

    # Порядок точности для каждого метода
    p = {
        1: 1,  # Левые прямоугольники
        2: 1,  # Правые прямоугольники
        3: 2,  # Средние прямоугольники
        4: 2,  # Метод трапеций
        5: 4   # Метод Симпсона
    }[method_choice]

    n = 4

    while True:
        I_n = method_func(f, a, b, n)
        I_2n = method_func(f, a, b, 2 * n)
        runge_error = abs(I_2n - I_n) / (2 ** p - 1) #рунге оценка погрешности

        print(f"\nРазбиение n = {n}")
        print(f"{method_name} (n): {I_n:.6f}")
        print(f"{method_name} (2n): {I_2n:.6f}")
        print(f"Оценка погрешности по правилу Рунге: {runge_error:.6f}")

        if runge_error < epsilon:
            integral = I_2n
            break

        n *= 2

    print("\nИтоговые результаты:")
    print(f"Достигнутая точность: {runge_error:.6f}")
    print(f"Число разбиений для достижения точности: {n * 2}")
    print(f"Значение интеграла методом {method_name}: {integral:.6f}")
    print("Точное решение:")
    integral_print(a, b)

    print("\nТаблица значений для последнего разбиения:")
    h = (b - a) / (n * 2)
    if method_choice == 3:  # Средние прямоугольники
        print("i\tx_i\t\ty_i\t\tx_{i-1/2}\t\ty_{i-1/2}")
        for i in range(n * 2 + 1):
            x_i = a + i * h
            y_i = f(x_i)
            if i > 0:
                x_mid = a + (i - 0.5) * h
                y_mid = f(x_mid)
            else:
                x_mid = ""
                y_mid = ""
            print(f"{i}\t{x_i:.6f}\t{y_i:.6f}\t{x_mid if i > 0 else '':<12}\t{y_mid if i > 0 else '':<12}")
    elif method_choice == 5:  # Симпсон
        print("i\tx_i\t\ty_i\tКоэффициент")
        for i in range(n * 2 + 1):
            x_i = a + i * h
            y_i = f(x_i)
            if i == 0 or i == n * 2:
                coeff = 1
            elif i % 2 == 1:
                coeff = 4
            else:
                coeff = 2
            print(f"{i}\t{x_i:.6f}\t{y_i:.6f}\t{coeff}")
    else:
        print("i\tx_i\t\ty_i")
        for i in range(n * 2 + 1):
            x_i = a + i * h
            y_i = f(x_i)
            print(f"{i}\t{x_i:.6f}\t{y_i:.6f}")

if __name__ == "__main__":
    calculate_integral()
