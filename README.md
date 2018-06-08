```
# instance method from object
# o.instanceMethod()
@method -
# o.instanceMethod(arg1, arg2)
@method arg1 arg2 -
@ o.instanceMethod(type1:arg1, type2:arg2)
@method type1:arg1 type2:arg2 -

# instance field from object
# o.instanceField
$field

# static method from object or class
# o.staticMethod()
@@method -
# o.staticMethod(arg1, arg2)
@@method arg1 arg2 -
# o.staticMethod(type1:arg1, type2:arg2)
@@method type1:arg1 type2:arg2 -

# static field from object or class
# o.staticField
$$field

# Constructor from class
# Clazz()
+ -
# Clazz(arg1, arg2)
+ arg1 arg2 -
# Clazz(type1:arg1, type2:arg2)
+ type1:arg1 type2:arg2
```

```
# show class meta, endwith >
>
```

```
# simple types
type=byte|short|int|long|bool|str|char|null
byte:1
short:1
integer:1
long:1
boolean:true
boolean:false
string:
string:hello
char:a
null:
```

```
# list types => ArrayList
type=byte[]|short[]|int[]|long[]|bool[]|str[]|char[]
# set types => HashSet
type=byte()|short()|int()|long()|bool()|str()|char()
```

```
self method -> father method -> grandfather method
self field => father field -> grandfather method
```

```
# select method
filterByParameterCount -> filterByType -> takeFirst
```

