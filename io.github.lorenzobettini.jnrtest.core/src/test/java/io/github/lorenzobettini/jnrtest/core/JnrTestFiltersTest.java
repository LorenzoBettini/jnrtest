package io.github.lorenzobettini.jnrtest.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link JnrTestFilters} class focusing on corner cases
 * and ensuring the expected behavior.
 */
class JnrTestFiltersTest {
    
    // Simple fake test class implementation
    private static class FakeTest extends JnrTest {
        
        public FakeTest(String description) {
            super(description);
        }
        
        @Override
        protected void specify() {
            // Empty implementation for testing
        }
    }
    
    // Empty runnable for testing
    private static final JnrTestRunnable EMPTY_RUNNABLE = () -> {};
    
    // Reusable test instances
    private final JnrTest calculatorTest = new FakeTest("Calculator");
    private final JnrTest stringTest = new FakeTest("String");
    private final JnrTestRunnableSpecification additionSpec = 
            new JnrTestRunnableSpecification("Addition", EMPTY_RUNNABLE);
    private final JnrTestRunnableSpecification subtractionSpec = 
            new JnrTestRunnableSpecification("Subtraction", EMPTY_RUNNABLE);
    
    @Test
    void testEmptyArrayWithAllClassesReturnsTrue() {
        // When we have an empty array of class filters
        JnrTestClassFilter filter = JnrTestFilters.allClasses();
        
        // Then the filter should accept any test class (return true)
        assertTrue(filter.include(calculatorTest));
    }
    
    @Test
    void testEmptyArrayWithAllSpecificationsReturnsTrue() {
        // When we have an empty array of specification filters
        JnrTestSpecificationFilter filter = JnrTestFilters.allSpecifications();
        
        // Then the filter should accept any specification (return true)
        assertTrue(filter.include(additionSpec));
    }
    
    @Test
    void testEmptyArrayWithAnyClassReturnsTrue() {
        // When we have an empty array of class filters
        JnrTestClassFilter filter = JnrTestFilters.anyClass();
        
        // Then the filter should accept any test class (return true)
        assertTrue(filter.include(calculatorTest));
    }
    
    @Test
    void testEmptyArrayWithAnySpecificationReturnsTrue() {
        // When we have an empty array of specification filters
        JnrTestSpecificationFilter filter = JnrTestFilters.anySpecification();
        
        // Then the filter should accept any specification (return true)
        assertTrue(filter.include(additionSpec));
    }
    
    @Test
    void testAllClassesReturnsTrueWhenAllFiltersReturnTrue() {
        // Given two class filters that both accept
        JnrTestClassFilter filter1 = testClass -> true;
        JnrTestClassFilter filter2 = testClass -> true;
        
        // When we combine them with allClasses
        JnrTestClassFilter combined = JnrTestFilters.allClasses(filter1, filter2);
        
        // Then the combined filter should accept (return true)
        assertTrue(combined.include(calculatorTest));
    }
    
    @Test
    void testAllClassesReturnsFalseWhenAnyFilterReturnsFalse() {
        // Given two class filters, one that rejects
        JnrTestClassFilter filter1 = testClass -> true;
        JnrTestClassFilter filter2 = testClass -> false;
        
        // When we combine them with allClasses
        JnrTestClassFilter combined = JnrTestFilters.allClasses(filter1, filter2);
        
        // Then the combined filter should reject (return false)
        assertFalse(combined.include(calculatorTest));
    }
    
    @Test
    void testAnyClassReturnsTrueWhenAnyFilterReturnsTrue() {
        // Given two class filters, one that accepts
        JnrTestClassFilter filter1 = testClass -> false;
        JnrTestClassFilter filter2 = testClass -> true;
        
        // When we combine them with anyClass
        JnrTestClassFilter combined = JnrTestFilters.anyClass(filter1, filter2);
        
        // Then the combined filter should accept (return true)
        assertTrue(combined.include(calculatorTest));
    }
    
    @Test
    void testAnyClassReturnsFalseWhenAllFiltersReturnFalse() {
        // Given two class filters that both reject
        JnrTestClassFilter filter1 = testClass -> false;
        JnrTestClassFilter filter2 = testClass -> false;
        
        // When we combine them with anyClass
        JnrTestClassFilter combined = JnrTestFilters.anyClass(filter1, filter2);
        
        // Then the combined filter should reject (return false)
        assertFalse(combined.include(calculatorTest));
    }
    
    @Test
    void testByClassDescriptionFilterMatchesCorrectPattern() {
        // When we create a filter for a specific pattern
        JnrTestClassFilter filter = JnrTestFilters.byClassDescription("Calculator.*");
        
        // Then the filter should accept a matching class
        assertTrue(filter.include(new FakeTest("Calculator Test")));
        
        // And reject a non-matching class
        assertFalse(filter.include(new FakeTest("String Utils")));
    }
    
    @Test
    void testBySpecificationDescriptionFilterMatchesCorrectPattern() {
        // When we create a filter for a specific pattern
        JnrTestSpecificationFilter filter = JnrTestFilters.bySpecificationDescription("Addition.*");
        
        // Then the filter should accept a matching specification
        assertTrue(filter.include(new JnrTestRunnableSpecification("Addition Test", EMPTY_RUNNABLE)));
        
        // And reject a non-matching specification
        assertFalse(filter.include(new JnrTestRunnableSpecification("Subtraction Test", EMPTY_RUNNABLE)));
    }
    
    @Test
    void testNotClassNegatesResult() {
        // Given a filter that accepts a specific class
        JnrTestClassFilter originalFilter = testClass -> 
            testClass.getDescription().equals("Calculator");
        
        // When we negate it
        JnrTestClassFilter negatedFilter = JnrTestFilters.notClass(originalFilter);
        
        // Then the results should be inverted
        assertTrue(originalFilter.include(calculatorTest));
        assertFalse(negatedFilter.include(calculatorTest));
        
        assertFalse(originalFilter.include(stringTest));
        assertTrue(negatedFilter.include(stringTest));
    }
    
    @Test
    void testNotSpecificationNegatesResult() {
        // Given a filter that accepts a specific specification
        JnrTestSpecificationFilter originalFilter = spec -> 
            spec.description().equals("Addition");
        
        // When we negate it
        JnrTestSpecificationFilter negatedFilter = JnrTestFilters.notSpecification(originalFilter);
        
        // Then the results should be inverted
        assertTrue(originalFilter.include(additionSpec));
        assertFalse(negatedFilter.include(additionSpec));
        
        assertFalse(originalFilter.include(subtractionSpec));
        assertTrue(negatedFilter.include(subtractionSpec));
    }
}
