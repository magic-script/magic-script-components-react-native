# Kotlin style guide

## Naming
- Start class and file names with a capital letter,
- Use camelCase for variable names,
- If a file contains only one class, please name the file the same as the class name

## Ordering in a class
1. Variables (public first)
2. Constructors
3. Methods (public first)

## Misc
- If a constructor or method has 4 or more parameters, write them in a separate line,
- If a condition of an `if` or `when` statement is multiline, always use curly braces around the body of the statement,
- Avoid too complicated `if` statements,
- Do no overuse `?.let { }` if just checking for nullity of an immutable variable,
- Do not use semicolons at the end of line,
- Remove unused code and imports


# XML rules for layouts
- Each property in a new line,
- Use view identifier only when required,
- The id should be the first property in a tag

Example:

    <ImageView
        android:id="@+id/imageview"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/img"
        android:contentDescription="@string/desc" />
		
# General coding conventions: https://kotlinlang.org/docs/reference/coding-conventions.html
