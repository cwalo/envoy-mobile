licenses(["notice"])  # Apache 2

load("@envoy//bazel:envoy_build_system.bzl", "envoy_package")
load("@build_bazel_rules_apple//apple:ios.bzl", "ios_application", "ios_framework", "ios_static_framework")
load("@io_bazel_rules_kotlin//kotlin/internal:toolchains.bzl", "define_kt_toolchain")

envoy_package()

ios_static_framework(
    name = "ios_framework",
    hdrs = [
        "//library/objective-c:envoy_framework_headers",
    ],
    bundle_name = "Envoy",
    minimum_os_version = "10.0",
    visibility = ["//visibility:public"],
    deps = ["//library/objective-c:envoy_objc_interface_lib"],
)

genrule(
    name = "ios_dist",
    srcs = ["//:ios_framework"],
    outs = ["ios_out"],
    cmd = """
unzip -o $< -d dist/
touch $@
""",
    stamp = True,
)

alias(
    name = "android_aar",
    actual = "//library:android_aar",
)

genrule(
    name = "android_dist",
    srcs = ["android_aar"],
    outs = ["android_out"],
    cmd = """
cp $< dist/envoy.aar
chmod 755 dist/envoy.aar
touch $@
""",
    stamp = True,
)

define_kt_toolchain(
    name = "kotlin_toolchain",
    jvm_target = "1.6",
)
