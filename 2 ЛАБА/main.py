import numpy as np
import sympy as sp
from sympy import sstr
import matplotlib.pyplot as plt


# -----------------------------------------------------------------------------
# 1. Функции-уравнения и их производные (для уравнений одной переменной)
# -----------------------------------------------------------------------------

def f1(x):
    return x ** 3 - x - 1


def df1(x):
    return 3 * x ** 2 - 1


def phi1(x):
    return (x + 1) ** (1 / 3)


def phi1_sym(x):
    return (x + 1) ** (sp.Rational(1, 3))


def f2(x):
    return np.sin(x) - 0.5 * x


def df2(x):
    return np.cos(x) - 0.5


def phi2(x):
    return 2.0 * np.sin(x)


def phi2_sym(x):
    return 2 * sp.sin(x)


def f3(x):
    return np.exp(x) - 3 * x


def df3(x):
    return np.exp(x) - 3


def phi3(x):
    if x <= 0:
        return 0.5
    return np.log(3.0 * x)


def phi3_sym(x):
    return sp.log(3 * x)


FUNCTIONS = {
    1: {'f': f1, 'df': df1, 'phi': phi1, 'phi_sym': phi1_sym, 'name': "x^3 - x - 1"},
    2: {'f': f2, 'df': df2, 'phi': phi2, 'phi_sym': phi2_sym, 'name': "sin(x) - 0.5*x"},
    3: {'f': f3, 'df': df3, 'phi': phi3, 'phi_sym': phi3_sym, 'name': "exp(x) - 3x"}
}


# -----------------------------------------------------------------------------
# 3. Функции ввода-вывода
# -----------------------------------------------------------------------------

def write_output_to_file(filename, text):
    with open(filename, 'w', encoding='utf-8') as f:
        f.write(text)


def finalize_output(output_choice, result_text):
    text_to_write = "\n".join(result_text)
    if output_choice == 'file':
        write_output_to_file("output.txt", text_to_write)
        print("Результаты сохранены в output.txt")
    else:
        print("\n" + text_to_write)


# -----------------------------------------------------------------------------
# 4. Методы решения уравнений (одной переменной)
# -----------------------------------------------------------------------------

def verify_interval_has_single_root(f, a, b):
    fa, fb = f(a), f(b)
    if fa == 0:
        return False, f"Внимание: f(a)=0 при a={a}."
    if fb == 0:
        return False, f"Внимание: f(b)=0 при b={b}."
    if fa * fb > 0:
        return False, "На концах интервала функция имеет одинаковый знак."
    return True, ""


def chord_method(f, a, b, tol=1e-6, max_iter=100):
    fa, fb = f(a), f(b)
    if fa * fb > 0:
        print("Предусловие метода хорд не выполнено (f(a) и f(b) одного знака).")
    x_left, x_right = a, b
    for i in range(max_iter):
        f_left, f_right = f(x_left), f(x_right)
        c = x_right - f_right * (x_right - x_left) / (f_right - f_left)
        if abs(f(c)) < tol:
            return c, i + 1, ""
        x_left, x_right = x_right, c
    return None, max_iter, "Метод хорд: превышено число итераций."


def approx_second_derivative(f, x, h=1e-5):
    return (f(x + h) - 2 * f(x) + f(x - h)) / (h ** 2)


def newton_method(f, df, a, b, tol=1e-6, max_iter=100):
    ddf_a = approx_second_derivative(f, a)
    ddf_b = approx_second_derivative(f, b)
    debug_msg = ""
    if f(a) * ddf_a > 0:
        x = a
        debug_msg += f"Начальное приближение: a = {a} (f(a)*f''(a) > 0)"
    elif f(b) * ddf_b > 0:
        x = b
        debug_msg += f"Начальное приближение: b = {b} (f(b)*f''(b) > 0)"
    else:
        x = 0.5 * (a + b)
        debug_msg += f"Начальное приближение: центр отрезка = {x}, (f(a)*f''(a) < 0) и (f(b)*f''(b) < 0)"
    for i in range(max_iter):
        fx, dfx = f(x), df(x)
        if abs(dfx) < 1e-14:
            return None, i, debug_msg + " | f'(x) слишком мало."
        x_new = x - fx / dfx
        if abs(x_new - x) < tol:
            return x_new, i + 1, debug_msg
        x = x_new
    return None, max_iter, debug_msg + " | Превышено число итераций."


def iteration_method(phi_sym_func, phi_num_func, a, b, x0, tol=1e-6, max_iter=100):
    x_sym = sp.Symbol('x', real=True)
    phi_sym_expr = phi_sym_func(x_sym)
    dphi_sym = sp.diff(phi_sym_expr, x_sym)
    xs = np.linspace(a, b, 50)
    max_dphi = max([abs(dphi_sym.subs(x_sym, xx)) for xx in xs])
    conv_msg = ""
    if max_dphi >= 1:
        conv_msg = f"WARNING: max|phi'(x)| = {max_dphi:.3f} >= 1; метод может не сходиться."
    else:
        conv_msg = f"Условие сходимости выполнено: max|phi'(x)| = {max_dphi:.3f} < 1."
    x_cur = x0
    for i in range(max_iter):
        x_next = phi_num_func(x_cur)
        if abs(x_next - x_cur) < tol:
            return x_next, i + 1, conv_msg
        x_cur = x_next
    return None, max_iter, conv_msg


def plot_function(f, a, b):
    xs = np.linspace(a, b, 400)
    ys = [f(x) for x in xs]
    plt.figure(figsize=(6, 4))
    plt.plot(xs, ys, label='f(x)')
    plt.axhline(0, color='black', lw=0.8)
    plt.title(f"График функции на [{a}, {b}]")
    plt.legend()
    plt.grid(True)
    plt.show()


def plot_function_with_boundaries(f, a, b):
    xs = np.linspace(a, b, 400)
    ys = [f(x) for x in xs]
    plt.figure(figsize=(6, 4))
    plt.plot(xs, ys, label='f(x)')
    plt.axvline(a, color='green', linestyle='--', label=f'a = {a}')
    plt.axvline(b, color='orange', linestyle='--', label=f'b = {b}')
    plt.axhline(0, color='black', lw=0.8)
    plt.title(f"График функции на интервале [{a}, {b}]")
    plt.legend()
    plt.grid(True)
    plt.show()


# -----------------------------------------------------------------------------
# 6. Ввод данных
# -----------------------------------------------------------------------------

def read_equation_input_file(filename):
    with open(filename, 'r', encoding='utf-8') as f:
        lines = [line.strip() for line in f if line.strip()]
    func_choice = int(lines[0])
    a, b = map(float, lines[1].split())
    tol = float(lines[2])
    output_choice = lines[3].lower()
    return func_choice, a, b, tol, output_choice


def read_equation_input_keyboard():
    print("Сначала отображается график функции.")
    print("Выберите функцию для решения уравнения:")
    for k, v in FUNCTIONS.items():
        print(f"{k}: {v['name']}")
    func_choice = int(input("Номер функции: "))
    if func_choice not in FUNCTIONS:
        return None
    data = FUNCTIONS[func_choice]
    plot_function(data['f'], -10, 10)
    a, b = map(float, input("Введите границы интервала (a b): ").split())
    tol = float(input("Введите точность (например 1e-6): "))
    output_choice = input("Куда выводить результат? (file/console): ").strip().lower()
    return func_choice, a, b, tol, output_choice


# -----------------------------------------------------------------------------
# Функции для решения систем методом простой итерации
# -----------------------------------------------------------------------------
def iteration_method_system(phi_sym_funcs, phi_num_funcs, x0, y0, tol=1e-6, max_iter=100, threshold=1e6):
    """
    Фиксированная итерация для системы двух уравнений:
    x = phi1(x,y), y = phi2(x,y).
    Если значения выходят за пределы threshold, процесс останавливается как расходящийся.
    Возвращает приближение, число итераций, вектор ошибок, константу q и сообщение.
    """
    # Символьные переменные для производных
    x_sym, y_sym = sp.symbols('x y', real=True)
    phi1_sym, phi2_sym = phi_sym_funcs

    # Вычисляем сумму модулей частных производных в (x0,y0)
    subs = {x_sym: x0, y_sym: y0}
    d11 = abs(sp.diff(phi1_sym, x_sym).subs(subs))  # (y/3)dx = 0
    d12 = abs(sp.diff(phi1_sym, y_sym).subs(subs))  # (y/3)dy = 1/3
    d21 = abs(sp.diff(phi2_sym, x_sym).subs(subs))  # ((x^3)/2-1)dx = (3*x^2)/2
    d22 = abs(sp.diff(phi2_sym, y_sym).subs(subs))  # ((x^3)/2-1)dy = 0

    print(d11+d12, " ", d21+d22)

    # Вывод φ-функций
    print(f"φ₁(x,y) = {phi1_sym}")
    print(f"φ₂(x,y) = {phi2_sym}")

    # производные φ-функций
    print("∂φ₁/∂x =", sp.diff(phi1_sym, x_sym))
    print("∂φ₁/∂y =", sp.diff(phi1_sym, y_sym))
    print("∂φ₂/∂x =", sp.diff(phi2_sym, x_sym))
    print("∂φ₂/∂y =", sp.diff(phi2_sym, y_sym))

    q = float(max(d11 + d12, d21 + d22))
    if q < 1:
        conv_msg = f"Условие сходимости выполнено: q = {q:.3f} < 1."
    else:
        conv_msg = f"WARNING: q = {q:.3f} >= 1; метод может расходиться."

    # Итерации
    errors = []
    x_cur, y_cur = x0, y0
    for i in range(1, max_iter + 1):
        x_next = phi_num_funcs[0](x_cur, y_cur)
        y_next = phi_num_funcs[1](x_cur, y_cur)

        # Проверка на расходящиеся значения
        if abs(x_next) > threshold or abs(y_next) > threshold:
            return (x_cur, y_cur), i - 1, errors, q, (
                f"ERROR: итерации вышли за пределы ±{threshold:.0f} на шаге {i}. Процесс расходится."
            )

        print(x_cur, y_cur)

        errx = abs(x_next - x_cur)
        erry = abs(y_next - y_cur)
        errors.append((errx, erry))

        # Критерий останова
        if max(errx, erry) < tol:
            return (x_next, y_next), i, errors, q, conv_msg + " Процесс сошёлся."

        x_cur, y_cur = x_next, y_next

    # Если не сошлось за max_iter
    return (x_cur, y_cur), max_iter, errors, q, (
        f"WARNING: не достигнута точность за {max_iter} итераций; "
        "возвращено последнее приближение"
    )


# -----------------------------------------------------------------------------
# 7. Основная логика
# -----------------------------------------------------------------------------

def main():
    problem_type = input("Что решаем? (equation/system): ").strip().lower()
    result_text = []

    if problem_type == 'equation':
        input_src = input("Считать данные из файла или клавиатуры? (file/keyboard): ").strip().lower()
        if input_src == 'file':
            func_choice, a, b, tol, output_choice = read_equation_input_file("input_equation.txt")
        else:
            user_input = read_equation_input_keyboard()
            if user_input is None:
                return
            func_choice, a, b, tol, output_choice = user_input

        data = FUNCTIONS.get(func_choice)
        if data is None:
            print("Некорректный номер функции.")
            return

        plot_function_with_boundaries(data['f'], a, b)
        f, df = data['f'], data['df']
        phi_num = data['phi']
        phi_sym = data['phi_sym']

        if a > b:
            a, b = b, a

        ok, msg = verify_interval_has_single_root(f, a, b)
        if not ok:
            result_text.append("Внимание: " + msg)

        root_ch, it_ch, msg_ch = chord_method(f, a, b, tol)
        if root_ch is not None:
            result_text.append(
                f"Метод хорд (a = {a}, b = {b}): корень = {root_ch:.6f}, f(root) = {f(root_ch):.6e}, итераций = {it_ch}")
        else:
            result_text.append("Метод хорд: " + msg_ch)

        root_nw, it_nw, newton_debug = newton_method(f, df, a, b, tol)
        if root_nw is not None:
            result_text.append(
                f"Метод Ньютона ({newton_debug}): корень = {root_nw:.6f}, f(root) = {f(root_nw):.6e}, итераций = {it_nw}")
        else:
            result_text.append("Метод Ньютона: не удалось найти корень. " + newton_debug)

        x0 = 0.5 * (a + b)
        root_it, it_it, iter_debug = iteration_method(phi_sym, phi_num, a, b, x0, tol)
        if root_it is not None:
            result_text.append(
                f"Метод итераций ({iter_debug}, начальное x0 = {x0}): корень = {root_it:.6f}, f(root) = {f(root_it):.6e}, итераций = {it_it}")
        else:
            result_text.append("Метод итераций: не удалось найти корень. " + iter_debug)

        finalize_output(output_choice, result_text)

    elif problem_type == 'system':
        print("Метод простой итерации для систем нелинейных уравнений")
        print("1) y = 3x;   y = x^3/2 - 1")
        print("2) x^2 + y^2 = 25;   y = x^3 - 2")
        choice = input("Выберите систему (1 или 2): ").strip()
        if choice not in ('1', '2'):
            print("Неверный выбор системы.")
            return
        tol = float(input("Введите точность (например 1e-6): "))

        #Настройка phi-функций и графика
        if choice == '1':
            x_s, y_s = sp.symbols('x y', real=True)

            # φ-функции (символьные)
            phi_sym = (y_s / 3,
                       x_s ** 3 / 2 - 1)

            # φ-функции (числовые)
            phi_num = (lambda x, y: y / 3,
                       lambda x, y: (x ** 3) / 2 - 1)

            xs = np.linspace(-3, 3, 400)
            plt.plot(xs, [3 * x for x in xs], label='y=3x')
            plt.plot(xs, [(x ** 3) / 2 - 1 for x in xs], label='y=x³/2 -1')
            title = 'Система 1: y=3x и y=x³/2 -1'
        else:  # система 2
            x_s, y_s = sp.symbols('x y', real=True)

            # Спрашиваем у пользователя, какую ветвь корня он хочет
            branch = input("Выберите ветвь для y = ±√(25 - x²) (введите '+' или '-'): ").strip()
            if branch == '+':
                phi2_sym = sp.sqrt(25 - x_s ** 2)
                phi2_num = lambda x, y: np.sqrt(max(0, 25 - x ** 2))
            else:
                phi2_sym = -sp.sqrt(25 - x_s ** 2)
                phi2_num = lambda x, y: -np.sqrt(max(0, 25 - x ** 2))

            # φ₁ остаётся без изменений
            phi1_sym = sp.root(y_s + 2, 3)
            phi1_num = lambda x, y: np.cbrt(y + 2)

            phi_sym = (phi1_sym, phi2_sym)
            phi_num = (phi1_num, phi2_num)

            # Рисуем оба варианта просто для наглядности
            xs = np.linspace(-5, 5, 400)
            plt.plot(xs, [np.sqrt(max(0, 25 - x ** 2)) for x in xs], label='+sqrt(25 - x²)')
            plt.plot(xs, [-np.sqrt(max(0, 25 - x ** 2)) for x in xs], label='-sqrt(25 - x²)')
            plt.plot(xs, [x ** 3 - 2 for x in xs], label='y = x³ - 2')
            title = 'Система 2: x² + y² = 25 и y = x³ - 2'

        plt.title(title)
        plt.axhline(0, color='black', linewidth=0.5)  # Ось X
        plt.axvline(0, color='black', linewidth=0.5)  # Ось Y
        plt.legend()
        plt.grid(True)
        plt.axis('equal')  # Сохраняем пропорции

        # Устанавливаем желаемые границы осей
        plt.xlim(-7, 7)  # От -10 до 10 по X
        plt.ylim(-7, 7)  # От -10 до 10 по Y

        plt.show()

        x0 = float(input("Введите начальное приближение x0: "))
        y0 = float(input("Введите начальное приближение y0: "))

        sol, iters, errors, q, message = iteration_method_system(
            phi_sym, phi_num, x0, y0, tol, max_iter=1000, threshold=1e6
        )

        print(f"q = {q:.3f}. {message}")
        x_sol, y_sol = sol
        print(f"Приближение: x = {x_sol:.6f}, y = {y_sol:.6f}")
        print(f"Итераций: {iters}")
        # print("Погрешности по шагам:")
        # for idx, (dx, dy) in enumerate(errors, 1):
        #     print(f"{idx}: |Δx|={dx:.2e}, |Δy|={dy:.2e}")

        # Вычисление невязок в системе
        try:
            if choice == '1':
                r1 = y_sol - 3 * x_sol
                r2 = y_sol - (x_sol ** 3) / 2 + 1
            else:
                r1 = x_sol ** 2 + y_sol ** 2 - 25
                r2 = y_sol - x_sol ** 3 + 2
            print(f"Невязки: f1={r1:.2e}, f2={r2:.2e}")
        except Exception:
            print("Невязки не удалось вычислить из-за переполнения.")


if __name__ == "__main__":
    main()
