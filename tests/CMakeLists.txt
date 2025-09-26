include(FetchContent)
FetchContent_Declare(
        googletest
        DOWNLOAD_EXTRACT_TIMESTAMP TRUE
        URL https://github.com/google/googletest/archive/refs/tags/v1.15.2.zip
)
# Avoid gtest changes flags itself
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

add_executable(lentz_unit_tests
        unit/test_core.cpp
)
target_include_directories(lentz_unit_tests
        PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}/unit
)
target_link_libraries(lentz_unit_tests
        PRIVATE
        lentz_backend         # pulls in analysis/runtime/ast transitively
        lentz_frontend        # if tests need parsing
        gtest_main
)

add_test(NAME unit_core COMMAND lentz_unit_tests)

# E2E smoke that also compiles the generated C++ and checks output
add_test(NAME e2e_generate_build_run
        COMMAND ${CMAKE_CURRENT_SOURCE_DIR}/e2e/run_e2e.sh
        $<TARGET_FILE:lentzc>
        ${CMAKE_BINARY_DIR}/gen
        ${CMAKE_SOURCE_DIR}/examples/hello/main.lz
)
