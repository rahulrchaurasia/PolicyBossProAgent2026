# Release keep rules, wired in through `optimization { keepRules { ... } }` in build.gradle.kts.

# R8 runs in partial mode (packageScope = androidx/kotlin/kotlinx). Its repackaging moved
# multifile-facade parts such as kotlin.text.StringsKt__StringBuilderKt into the default
# package while their package-private superclass (StringsKt__IndentKt) stayed in kotlin.text.
# At startup this failed with: IllegalAccessError: Class kotlin.text.k extended by class ip2
# is inaccessible. Keeping package names stops R8 from moving classes across packages, so
# package-private access stays valid. Shrinking, optimization and class renaming still apply.
-keeppackagenames androidx.**,kotlin.**,kotlinx.**
