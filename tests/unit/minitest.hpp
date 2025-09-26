#pragma once
#include <functional>
#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>

namespace minitest {
    struct TestCase {
        std::string name;
        std::function<void()> fn;
    };
    inline std::vector<TestCase>& registry() {
        static std::vector<TestCase> r;
        return r;
    }
    struct Registrar {
        Registrar(const char* name, std::function<void()> fn) {
            registry().push_back({name, std::move(fn)});
        }
    };
    inline int run_all() {
        int failed = 0;
        for (auto& t : registry()) {
            try {
                t.fn();
                std::cout << "[ OK ] " << t.name << "\n";
            } catch (const std::exception& e) {
                std::cout << "[FAIL] " << t.name << " - " << e.what() << "\n";
                ++failed;
            } catch (...) {
                std::cout << "[FAIL] " << t.name << " - unknown exception\n";
                ++failed;
            }
        }
        std::cout << "Ran " << registry().size() << " test(s), failures: " << failed << "\n";
        return failed == 0 ? 0 : 1;
    }

#define TEST(Name) \
static void Name(); \
static ::minitest::Registrar Name##_registrar(#Name, &Name); \
static void Name()

#define EXPECT_TRUE(cond) do { if (!(cond)) throw std::runtime_error(std::string("EXPECT_TRUE failed: ") + #cond); } while(0)
#define EXPECT_EQ(a,b) do { if (!((a)==(b))) throw std::runtime_error(std::string("EXPECT_EQ failed: ") + #a " == " #b); } while(0)

} // namespace minitest
