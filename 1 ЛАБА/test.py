import numpy as np
from itertools import permutations

def read_matrix_from_file(filename):
    try:
        with open(filename, 'r') as file:
            # Чтение размерности матрицы
            n = int(file.readline().strip())  # Убираем лишние пробелы и символы новой строки
            matrix = []
            # Чтение матрицы A
            for _ in range(n):
                row = list(map(float, file.readline().strip().split()))
                matrix.append(row)
            # Чтение вектора b
            b = list(map(float, file.readline().strip().split()))
            # Чтение точности
            tolerance = float(file.readline().strip())
            return np.array(matrix), np.array(b), n, tolerance
    except FileNotFoundError:
        print(f"Ошибка: Файл '{filename}' не найден.")
        return None, None, None, None
    except ValueError:
        print(f"Ошибка: Файл '{filename}' имеет неправильный формат.")
        return None, None, None, None

def read_matrix_from_keyboard(n):
    matrix = []
    print("Введите коэффициенты матрицы построчно:")
    for _ in range(n):
        row = list(map(float, input().split()))
        matrix.append(row)
    b = np.array(list(map(float, input("Введите вектор правых частей (b): ").split())))
    tolerance = float(input("Введите точность: "))
    return np.array(matrix), b, tolerance


def check_diagonal_dominance(matrix, n):
    """Проверяет, является ли матрица диагонально доминирующей."""
    for i in range(n):
        diagonal_element = abs(matrix[i][i])
        sum_of_other_elements = sum(abs(matrix[i][j]) for j in range(n) if j != i)
        if diagonal_element < sum_of_other_elements:
            return False
    return True

def rearrange_for_diagonal_dominance(matrix, b, n):
    """Перебирает все возможные перестановки строк для достижения диагонального преобладания."""
    # Генерируем все возможные перестановки индексов строк
    for perm in permutations(range(n)):
        # Создаем копии матрицы и вектора b
        new_matrix = matrix.copy()
        new_b = b.copy()

        # Применяем перестановку
        for i in range(n):
            new_matrix[i] = matrix[perm[i]]
            new_b[i] = b[perm[i]]

        # Проверяем, достигнуто ли диагональное преобладание
        if check_diagonal_dominance(new_matrix, n):
            return new_matrix, new_b

    return matrix, b

def jacobi_method(A, b, tolerance):
    n = len(b)
    x = np.zeros(n)
    x_new = np.zeros(n)
    iterations = 0
    errors = []  # Вектор погрешностей
    max_iterations = 1000  # Максимальное количество итераций

    for _ in range(max_iterations):
        for i in range(n):
            sum_ = sum(A[i][j] * x[j] for j in range(n) if j != i)
            x_new[i] = (b[i] - sum_) / A[i][i]

        # Вычисление погрешности (норма разности между текущим и предыдущим приближением)
        error = np.linalg.norm(x_new - x)
        errors.append(error)

        if error < tolerance:
            x = x_new.copy()  # Обновляем x на последней итерации
            break

        x = x_new.copy()
        iterations += 1

    return x, iterations, errors

def main():
    choice = input("Введите '1' для ввода данных с клавиатуры или '2' для ввода из файла: ")

    if choice == '1':
        n = int(input("Введите размерность матрицы (n <= 20): "))
        if n > 20:
            print("Размерность матрицы должна быть <= 20.")
            return
        A, b, tolerance = read_matrix_from_keyboard(n)
    elif choice == '2':
        filename = input("Введите имя файла: ")
        A, b, n, tolerance = read_matrix_from_file(filename)
        if A is None:  # Если файл не найден или имеет неправильный формат, завершаем программу
            return
    else:
        print("Неверный выбор.")
        return

    # Проверка и перестановка для диагонального преобладания
    if not check_diagonal_dominance(A, n):
        print("Диагональное преобладание отсутствует. Пытаемся переставить строки...")
        A, b = rearrange_for_diagonal_dominance(A, b, n)
        if not check_diagonal_dominance(A, n):
            print("Невозможно достичь диагонального преобладания. Метод может не сойтись.")
        else:
            print("Диагональное преобладание достигнуто после перестановки строк.")

    print (A)
    print("норма")
    ans = 0
    sum = 0;
    for i in range(n):
        for j in range(n):
            if i != j:
                sum += A[i][j] / A[i][i]
        ans = max(sum, ans)
        sum = 0;
    print(ans)

    # Вывод нормы матрицы (используем спектральную норму)
    matrix_norm = np.linalg.norm(A, ord=2)
    print(f"\nНорма матрицы A (спектральная норма): {matrix_norm}")

    print(f"Используемая точность: {tolerance}")

    x, iterations, errors = jacobi_method(A, b, tolerance)

    print("\nВектор неизвестных (x):")
    print(x)
    print(f"\nКоличество итераций: {iterations}")

    # Вывод вектора погрешностей
    print("\nВектор погрешностей (на каждой итерации):")
    print([float(round(error, 6)) for error in errors])

    # Вычисление вектора невязок
    residual = np.dot(A, x) - b
    print("\nВектор невязок:")
    print(residual)

    # Сравнение с решением, полученным с помощью библиотеки numpy
    x_lib = np.linalg.solve(A, b)
    print("\nРешение, полученное с помощью библиотеки numpy:")
    print(x_lib)


if __name__ == "__main__":
    main()