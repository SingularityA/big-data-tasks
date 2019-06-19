### Задача 1

Сгенерировать бинарный файл (min 2Gb), состоящий из случайных 32-рязрядных 
беззнаковых целых чисел (big endian).

Посчитать их сумму:
1. Последовательно
2. Многопоточно + memory mapped files

Сравнить время работы.

---
###### На системе AMD FX(tm)-4300 Quad-Core Processor 3.80 GHz 2(4) ядра, 16 RAM

    Benchmark                              Mode  Cnt      Score       Error  Units
    SummationBenchmarks.memoryMappedAdder  avgt    5  24930,099 ± 28612,167  ms/op
    SummationBenchmarks.sequentialAdder    avgt    5  33435,244 ±   796,352  ms/op
  
---
###### На системе Intel(R) Core(TM) i7-6700HQ CPU 2.60 GHz 4(8) ядра, 8 RAM

    Benchmark                              Mode  Cnt      Score      Error  Units
    SummationBenchmarks.memoryMappedAdder  avgt    5  10263,986 ±  226,520  ms/op
    SummationBenchmarks.sequentialAdder    avgt    5  28483,712 ± 1732,589  ms/op

### Задача 2

Сгенерировать файл, содержащий 2000 128-битных случайных целых чисел, 
каждое число на отдельной строке.

Посчитать, какое суммарное количество простых множителей присутствует 
при факторизации всех чисел. 
При реализации нужно использовать операции с длинной арифметикой (BigInteger и т.д.)

Реализовать подсчет:
1. простым последовательным алгоритмом
2. многопоточно, с использованием примитивов синхронизации
3. с помощью Akka (или аналога)
4. c помощью RxJava (или аналога)

Измерить время выполнения для каждого случая. 
Использовать уровень параллельности в соответствии с числом ядер вашего CPU.

---
###### На системе AMD FX(tm)-4300 Quad-Core Processor 3.80 GHz 2(4) ядра, 16 RAM

    Benchmark                                  Mode  Cnt      Score     Error  Units
    FactorizationBenchmarks.akkaActorsCounter  avgt    5  14027,945 ± 434,810  ms/op
    FactorizationBenchmarks.forkJoinCounter    avgt    5  10351,571 ± 137,874  ms/op
    FactorizationBenchmarks.rxCounter          avgt    5  13626,931 ± 133,379  ms/op
    FactorizationBenchmarks.sequentialCounter  avgt    5  27426,060 ±  77,740  ms/op

---
###### На системе Intel(R) Core(TM) i7-6700HQ CPU 2.60 GHz 4(8) ядра, 8 RAM

    Benchmark                                  Mode  Cnt      Score      Error  Units
    FactorizationBenchmarks.akkaActorsCounter  avgt    5   5996,157 ±  153,858  ms/op
    FactorizationBenchmarks.forkJoinCounter    avgt    5   6427,210 ± 2288,018  ms/op
    FactorizationBenchmarks.rxCounter          avgt    5   7464,831 ±   85,957  ms/op
    FactorizationBenchmarks.sequentialCounter  avgt    5  25005,999 ±  569,943  ms/op


### Задача 3

Написать скрипт, который скачивает все данные прошедших президентских выборов 
для всех избирательных участков.

Входная точка по [ссылке](http://www.vybory.izbirkom.ru/region/region/izbirkom?action=show&root=1&tvd=100100084849066&vrn=100100084849062&region=0&global=1&sub_region=0&prver=0&pronetvd=null&vibid=100100084849066&type=227). 
Затем нужно перейти на сайты региональных избирательных комиссий. 
Результаты нужно сохранить в cvs-файл, sqlite базе данных или parquet-файле. 

В итоге должна получиться таблица с полями:
- название региона
- название ТИК
- номер УИК
- 20 стандартных полей из итогового протокола

Далее, используя Spark:
- найти явку (%) по всем регионам, результат отсортировать по убыванию
- выбрать любимого кандидата и найти тот избиратльный участок, 
на котором он получил наибольший результат 
(учитывать участки на которых проголосовало больше 300 человек)
- найти регион, где разница между ТИК с наибольшей явкой и наименьшей максимальна
- посчитать дисперсию по явке для каждого региона (учитывать УИК)
- для каждого кандидата посчитать таблицу: результат 
(%, округленный до целого) - количество УИК, на которых кандидат получил данный результат.