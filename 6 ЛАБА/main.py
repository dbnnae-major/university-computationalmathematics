import numpy as np
import matplotlib.pyplot as plt
import pandas as pd


# Уравнения и их точные решения
def f1(x, y): return y - x ** 2 + 1


def exact1(x): return (x + 1) ** 2 - 0.5 * np.exp(x)


def f2(x, y): return y * np.cos(x)


def exact2(x, x0, y0): return y0 * np.exp(np.sin(x) - np.sin(x0))


def f3(x, y): return x * np.exp(-x ** 2)


def exact3(x): return 0.5 * (1 - np.exp(-x ** 2))  # при y(0)=0


functions = {
    "1": ("y - x^2 + 1", f1, lambda x, x0, y0: exact1(x)),
    "2": ("y * cos(x)", f2, lambda x, x0, y0: exact2(x, x0, y0)),
    "3": ("x * exp(-x^2)", f3, lambda x, x0, y0: exact3(x))
}


# Методы
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
        return y + h / 6 * (k1 + 2 * k2 + 2 * k3 + k4)

    X, Y = [x0], [y0]
    for _ in range(3):
        y0 = rk4(x0, y0)
        x0 += h
        X.append(x0)
        Y.append(y0)

    while round(X[-1] + h, 10) <= xn:
        f0, f1_, f2_, f3_ = [f(X[i], Y[i]) for i in range(-4, 0)]
        yp = Y[-1] + h / 24 * (55 * f3_ - 59 * f2_ + 37 * f1_ - 9 * f0)  # предиктор, таблицы АДАМСА-БАШФОРТА
        fc = f(X[-1] + h, yp)
        yc = Y[-1] + h / 24 * (9 * fc + 19 * f3_ - 5 * f2_ + f1_)  # корректор, таблицы АДАМСА-МУЛТОНА
        X.append(round(X[-1] + h, 10))
        Y.append(yc)
    return np.array(X), np.array(Y)


# Проверка по правилу Рунге на последней точке - подбор подходящего h
# def find_valid_h(f, method, x0, y0, h, xn, p, eps, s):
#     while True:
#         _, y_h = method(f, x0, y0, h, xn)
#         _, y_h2 = method(f, x0, y0, h / 2, xn)
#         runge_err = abs(y_h[-1] - y_h2[-1]) / (2 ** p - 1)
#         if runge_err < eps:
#             print(f"\n{s} Найден подходящий шаг h = {h:.6f}, y1 = : {y_h[-1]:.6f}, y2 = : {y_h2[-1]:.6f}")
#             return h
#         print(
#             f"{s} Погрешность по Рунге = {runge_err:.2e}, y1 = : {y_h[-1]:.6f}, уменьшаем шаг: h = {h:.6f} → {h / 2:.6f}")
#         h /= 2

def find_valid_h_strict(f, method, exact, x0, y0, h, xn, p, eps, s, h_input):
    while True:
        x_h, y_h = method(f, x0, y0, h, xn)
        x_h2, y_h2 = method(f, x0, y0, h / 2, xn)

        # Оставляем только узлы кратные h_input
        mask = np.isclose((x_h - x0) % h_input, 0, atol=1e-8)
        mask2 = np.isclose((x_h2 - x0) % h_input, 0, atol=1e-8)

        y1 = y_h[mask]
        y2 = y_h2[mask2]
        x_vals = x_h[mask]

        min_len = min(len(y1), len(y2))
        runge_errs = np.abs(y2[:min_len] - y1[:min_len]) / (2 ** p - 1)
        max_err = np.max(runge_errs)

        if max_err < eps:
            print("Подробности по узлам:")
            for i in range(min_len):
                print(f"  x = {x_vals[i]:.4f}, y(h) = {y1[i]:.6f}, y(h/2) = {y2[i]:.6f}, Δ = {runge_errs[i]:.2e}")
            print(f"\n{s} Найден шаг h = {h:.6f}, максимальная ошибка по Рунге на узлах: {max_err:.2e} < eps")
            return h
        # print(f"{s} Ошибка по Рунге на узлах = {max_err:.2e} > eps = {eps}, уменьшаем шаг: h = {h:.6f} → {h/2:.6f}")
        print(f"{s} Ошибка по Рунге = {max_err:.2e}, уменьшаем шаг: h = {h:.6f} → {h/2:.6f}")

        h /= 2

show_all_points = False
# Ввод
print("Выберите одно из ОДУ:")
for key, (desc, _, _) in functions.items():
    print(f"{key}: y' = {desc}")
choice = input("Введите номер уравнения: ").strip()
desc, f, exact = functions[choice]

x0 = float(input("x0 = "))
y0 = float(input("y0 = "))
xn = float(input("xn = "))
h_input = float(input("Шаг h = "))
eps = float(input("Точность eps (для Рунге): "))

print(f"\nВыбранное ОДУ: y' = {desc}")
print(f"Начальные условия: x0 = {x0}, y0 = {y0}")
print(f"Интервал: [{x0}, {xn}], шаг h = {h_input}, точность eps = {eps}")

print("\nФормулы методов:")
print("Метод Эйлера:       y_{n+1} = y_n + h * f(x_n, y_n)")
print("Улучшенный Эйлер:   y_{n+1} = y_n + h/2 * (f(x_n, y_n) + f(x_{n+1}, y^*))")
print("Метод Адамса:       предиктор-корректор 4-го порядка\n")

# Вычисления
# h = find_valid_h(f, euler_method, x0, y0, h_input, xn, p=1, eps=eps, s="Метод Эйлера:")
# h = find_valid_h(f, improved_euler, x0, y0, h, xn, p=2, eps=eps, s="Метод улучшенного Эйлера:")
h = find_valid_h_strict(f, euler_method, exact, x0, y0, h_input, xn, p=1, eps=eps, s="Метод Эйлера:", h_input=h_input)

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

x_nodes = np.arange(x0, xn + h_input, h_input)
mask = np.isclose(x_vals[:, None], x_nodes, atol=1e-4).any(axis=1)
x_table = x_vals[mask]
table = pd.DataFrame({
    "x": x_table,
    "Эйлер": y_e[mask],
    "Δ Эйлер": np.abs(y_exact[mask] - y_e[mask]),
    "Улучш. Эйлер": y_ie[mask],
    "Δ Улучш. Эйлер": np.abs(y_exact[mask] - y_ie[mask]),
    "Адамса": y_ad[mask],
    "Δ Адамса": np.abs(y_exact[mask] - y_ad[mask]),
    "Точное": y_exact[mask]
})
pd.set_option('display.expand_frame_repr', False)
print("\nТаблица значений:")
print(table)

# pd.set_option('display.expand_frame_repr', False)
# # pd.set_option('display.max_columns', None)
# # pd.set_option('display.width', None)
# table = pd.DataFrame({
#     "x": x_vals,
#     "Эйлер": y_e,
#     "Δ Эйлер": np.abs(y_exact - y_e),
#     "Улучш. Эйлер": y_ie,
#     "Δ Улучш. Эйлер": np.abs(y_exact - y_ie),
#     "Адамса": y_ad,
#     "Δ Адамса": np.abs(y_exact - y_ad),
#     "Точное": y_exact
# })
# print("\nТаблица значений:")
# print(table)

# Графики
plt.plot(x_vals, y_exact, 'k-', label="Точное решение")
plt.plot(x_vals, y_e, 'r--', label="Метод Эйлера")
plt.plot(x_vals, y_ie, 'g-.', label="Улучш. Эйлер")
plt.plot(x_vals, y_ad, 'b-', label="Адамса (ПК)")
plt.xlabel("x")
plt.ylabel("y")
plt.title("Сравнение численного и точного решения")
plt.grid(True)
plt.legend()
plt.show()

# Оценка точности Адамса
adams_error = np.max(np.abs(y_exact[:min_len] - y_ad))
print(f"\nМаксимальная погрешность метода Адамса: ε = {adams_error:.6e}")

# # Рунге на КОНЦЕ интервала
# _, yh = euler_method(f, x0, y0, h, xn)
# _, yh2 = euler_method(f, x0, y0, h / 2, xn)
#
# runge_last = abs(yh[-1] - yh2[-1]) / (2 ** 1 - 1)
# print(f"\n Погрешность по правилу Рунге на последней точке (y_n): {runge_last:.6e}")
#
# if runge_last > eps:
#     print(" Погрешность на конце интервала превышает ε. Уменьшите шаг h.")
# else:
#     print(" Погрешность на конце интервала в пределах ε. Всё хорошо.")

# Правило Рунге
# def runge_check(f, method, x0, y0, h, xn, p):
#     _, y1 = method(f, x0, y0, h, xn)
#     _, y2 = method(f, x0, y0, h / 2, xn)
#     y2_half = y2[::2]
#     min_len = min(len(y1), len(y2_half))
#     return abs(y2_half[:min_len] - y1[:min_len]) / (2 ** p - 1)
# # Рунге
# errors = runge_check(f, euler_method, x0, y0, h, xn, p=1)
# max_runge_error = np.max(errors)
# print("\nОценка точности по правилу Рунге (метод Эйлера):")
# print(f"Максимальная ошибка по Рунге: {max_runge_error:.6e}")
# print(f"Ошибки по каждому шагу:\n{errors}")
# # Проверка на превышение точности
# if max_runge_error > eps:
#     print("\nПогрешность по правилу Рунге превышает ε!")
#     print("Рекомендуется уменьшить шаг h и пересчитать.")
# else:
#     print("\nПогрешность по правилу Рунге не превышает ε. Точность допустимая.")
