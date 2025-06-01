import numpy as np
import matplotlib.pyplot as plt
import pandas as pd

# Уравнения и их точные решения
def f1(x, y): return y - x**2 + 1
def exact1(x): return (x + 1)**2 - 0.5 * np.exp(x)

def f2(x, y): return y * np.cos(x)
def exact2(x, x0, y0): return y0 * np.exp(np.sin(x) - np.sin(x0))

def f3(x, y): return x * np.exp(-x**2)
def exact3(x): return 0.5 * (1 - np.exp(-x**2))  # при y(0)=0

functions = {
    "1": ("y - x^2 + 1", f1, lambda x, x0, y0: exact1(x)),
    "2": ("y * cos(x)", f2, lambda x, x0, y0: exact2(x, x0, y0)),
    "3": ("x * exp(-x^2)", f3, lambda x, x0, y0: exact3(x))
}

# Численные методы
def euler_method(f, x0, y0, h, xn):
    X, Y = [x0], [y0]
    steps = int(round((xn - x0) / h))
    for _ in range(steps):
        y0 += h * f(x0, y0)
        x0 += h
        X.append(x0)
        Y.append(y0)
    return np.array(X), np.array(Y)

def improved_euler(f, x0, y0, h, xn):
    X, Y = [x0], [y0]
    steps = int(round((xn - x0) / h))
    for _ in range(steps):
        k1 = f(x0, y0)
        k2 = f(x0 + h, y0 + h * k1)
        y0 += h / 2 * (k1 + k2)
        x0 += h
        X.append(x0)
        Y.append(y0)
    return np.array(X), np.array(Y)

def adams_method(f, x0, y0, h, xn):
    def rk4(x, y):
        k1 = f(x, y)
        k2 = f(x + h / 2, y + h / 2 * k1)
        k3 = f(x + h / 2, y + h / 2 * k2)
        k4 = f(x + h, y + h * k3)
        return y + h / 6 * (k1 + 2*k2 + 2*k3 + k4)

    X, Y = [x0], [y0]
    for _ in range(3):
        y0 = rk4(x0, y0)
        x0 += h
        X.append(x0)
        Y.append(y0)

    while round(X[-1] + h, 10) <= xn:
        f0, f1_, f2_, f3_ = [f(X[i], Y[i]) for i in range(-4, 0)]
        yp = Y[-1] + h / 24 * (55*f3_ - 59*f2_ + 37*f1_ - 9*f0) #предиктор
        fc = f(X[-1] + h, yp)
        yc = Y[-1] + h / 24 * (9*fc + 19*f3_ - 5*f2_ + f1_) #корректор
        X.append(round(X[-1] + h, 10))
        Y.append(yc)
    return np.array(X), np.array(Y)

# Ввод
print("Выберите одно из ОДУ:")
for key, (desc, _, _) in functions.items():
    print(f"{key}: y' = {desc}")
choice = input("Введите номер уравнения: ").strip()
desc, f, exact = functions[choice]

x0 = float(input("x0 = "))
y0 = float(input("y0 = "))
xn = float(input("xn = "))
h = float(input("Шаг h = "))
eps = float(input("Точность eps (для Рунге): "))

# Численные методы
x_e, y_e = euler_method(f, x0, y0, h, xn)
x_ie, y_ie = improved_euler(f, x0, y0, h, xn)
x_ad, y_ad = adams_method(f, x0, y0, h, xn)

min_len = min(len(x_e), len(x_ie), len(x_ad))
x_vals = x_e[:min_len]
y_e = y_e[:min_len]
y_ie = y_ie[:min_len]
y_ad = y_ad[:min_len]

# Точное решение
y_exact = exact(x_vals, x0, y0)

# Таблица

# pd.set_option("display.max_rows", None)
table = pd.DataFrame({
    "x": x_vals,
    "Эйлер": y_e,
    "Улучш. Эйлер": y_ie,
    "Адамса": y_ad,
    "Точное": y_exact
})
print(table)

# Графики
plt.plot(x_vals, y_exact, 'k-', label="Точное решение")
plt.plot(x_vals, y_e, 'r--', label="Метод Эйлера")
plt.plot(x_vals, y_ie, 'g-.', label="Улучш. Эйлера")
plt.plot(x_vals, y_ad, 'b-', label="Адамса (ПК)")
plt.xlabel("x")
plt.ylabel("y")
plt.title("Сравнение численного и точного решения")
plt.grid(True)
plt.legend()
plt.show()

# Оценка по правилу Рунге
def runge_check(f, method, x0, y0, h, xn, p):
    _, y1 = method(f, x0, y0, h, xn)
    _, y2 = method(f, x0, y0, h / 2, xn)
    y2_half = y2[::2]
    min_len = min(len(y1), len(y2_half))
    return abs(y2_half[:min_len] - y1[:min_len]) / (2 ** p - 1)

# Оценка точности для метода Адамса по формуле: ε = max|y_точн - y_числ|
adams_error = np.max(np.abs(y_exact[:min_len] - y_ad))
print(f"Максимальная погрешность метода Адамса: ε = {adams_error:.6e}")

# Оценка по правилу Рунге на всем интервале
errors = runge_check(f, euler_method, x0, y0, h, xn, p=1)
max_runge_error = np.max(errors)
print("\nОценка точности по правилу Рунге (метод Эйлера):")
print(f"Максимальная ошибка по Рунге: {max_runge_error:.6e}")
print(f"Все ошибки на каждом шаге:\n{errors}")

if max_runge_error > eps:
    print("\n Погрешность по правилу Рунге превышает допустимую ε!")
    print("Рекомендуется уменьшить шаг h и пересчитать.")
else:
    print("\n✅ Погрешность по правилу Рунге не превышает ε. Точность допустимая.")
