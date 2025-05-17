package io.github.lorenzobettini.jnrtest.core;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link JnrTestFilters} class focusing on corner cases
 * and ensuring the expected behavior.
 */
class JnrTestFiltersTest {
    
    @Test
    void testEmptyArrayWithAllClassesReturnsTrue() {
        // When we have an empty array of class filters
        JnrTestClassFilter filter = JnrTestFilters.allClasses();
        JnrTest testClass = mock(JnrTest.class);
        
        // Then the filter should accept any test class (return true)
        assertTrue(filter.include(testClass));
    }
    
    @Test
    void testEmptyArrayWithAllSpecificationsReturnsTrue() {
        // When we have an empty array of specification filters
        JnrTestSpecificationFilter filter = JnrTestFilters.allSpecifications();
        JnrTestRunnableSpecification specification = mock(JnrTestRunnableSpecification.class);
        
        // Then the filter should accept any specification (return true)
        assertTrue(filter.include(specification));
    }
    
    @Test
    void testEmptyArrayWithAnyClassReturnsTrue() {
        // When we have an empty array of class filters
        JnrTestClassFilter filter = JnrTestFilters.anyClass();
        JnrTest testClass = mock(JnrTest.class);
        
        // Then the filter should accept any test class (return true)
        assertTrue(filter.include(testClass));
    }
    
    @Test
    void testEmptyArrayWithAnySpecificationReturnsTrue() {
        // When we have an empty array of specification filters
        JnrTestSpecificationFilter filter = JnrTestFilters.anySpecification();
        JnrTestRunnableSpecification specification = mock(JnrTestRunnableSpecification.class);
        
        // Then the filter should accept any specification (return true)
        assertTrue(filter.include(specification));
    }
    
    @Test
    void testAllClassesReturnsTrueWhenAllFiltersReturnTrue() {
        // Given two class filters that both accept
        JnrTestClassFilter filter1 = testClass -> true;
        JnrTestClassFilter filter2 = testClass -> true;
        
        // When we combine them with allClasses
        JnrTestClassFilter combined = JnrTestFilters.allClasses(filter1, filter2);
        JnrTest testClass = mock(JnrTest.class);
        
        // Then the combined filter should accept (return true)
        assertTrue(combined.include(testClass));
    }
    
    @Test
    void testAllClassesReturnsFalseWhenAnyFilterReturnsFalse() {
        // Given two class filters, one that rejects
        JnrTestClassFilter filter1 = testClass -> true;
        JnrTestClassFilter filter2 = testClass -> false;
        
        // When we combine them with allClasses
        JnrTestClassFilter combined = JnrTestFilters.allClasses(filter1, filter2);
        JnrTest testClass = mock(JnrTest.class);
        
        // Then the combined filter should reject (return false)
        assertFalse(combined.include(testClass));
    }
    
    @Test
    void testAnyClassReturnsTrueWhenAnyFilterReturnsTrue() {
        // Given two class filters, one that accepts
        JnrTestClassFilter filter1 = testClass -> false;
        JnrTestClassFilter filter2 = testClass -> true;
        
        // When we combine them with anyClass
        JnrTestClassFilter combined = JnrTestFilters.anyClass(filter1, filter2);
        JnrTest testClass = mock(JnrTest.class);
        
        // Then the combined filter should accept (return true)
        assertTrue(combined.include(testClass));
    }
    
    @Test
    void testAnyClassReturnsFalseWhenAllFiltersReturnFalse() {
        // Given two class filters that both reject
        JnrTestClassFilter filter1 = testClass -> false;
        JnrTestClassFilter filter2 = testClass -> false;
        
        // When we combine them with anyClass
        JnrTestClassFilter combined = JnrTestFilters.anyClass(filter1, filter2);
        JnrTest testClass = mock(JnrTest.class);
        
        // Then the combined filter should reject (return false)
        assertFalse(combined.include(testClass));
    }
    
    @Test
    void testByClassDescriptionFilterMatchesCorrectPattern() {
        // Given a test class with a specific description
        JnrTest testClass = mock(JnrTest.class);
        when(testClass.getDescription()).thenReturn("Calculator Test");
        
        // When we create a filter for that pattern
        JnrTestClassFilter filter = JnrTestFilters.byClassDescription("Calculator.*");
        
        // Then the filter should accept the matching class
        assertTrue(filter.include(testClass));
        
        // And reject a non-matching class
        when(testClass.getDescription()).thenReturn("String Utils");
        assertFalse(filter.include(testClass));
    }
    
    @Test
    void testBySpecificationDescriptionFilterMatchesCorrectPattern() {
        // Given a test specification with a specific description
        JnrTestRunnableSpecification specification = mock(JnrTestRunnableSpecification.class);
        when(specification.description()).thenReturn("Addition Test");
        
        // When we create a filter for that pattern
        JnrTestSpecificationFilter filter = JnrTestFilters.bySpecificationDescription("Addition.*");
        
        // Then the filter should accept the matching specification
        assertTrue(filter.include(specification));
        
        // And reject a non-matching specification
        when(specification.description()).thenReturn("Subtraction Test");
        assertFalse(filter.include(specification));
    }
    
    @Test
    void testNotClassNegatesResult() {
        // Given a filter that accepts a specific class
        JnrTest calculatorTest = mock(JnrTest.class);
        when(calculatorTest.getDescription()).thenReturn("Calculator");
        
        JnrTestClassFilter originalFilter = testClass -> 
            testClass.getDescription().equals("Calculator");
        
        // When we negate it
        JnrTestClassFilter negatedFilter = JnrTestFilters.notClass(originalFilter);
        
        // Then the results should be inverted
        assertTrue(originalFilter.include(calculatorTest));
        assertFalse(negatedFilter.include(calculatorTest));
        
        JnrTest stringTest = mock(JnrTest.class);
        when(stringTest.getDescription()).thenReturn("String");
        
        assertFalse(originalFilter.include(stringTest));
        assertTrue(negatedFilter.include(stringTest));
    }
    
    @Test
    void testNotSpecificationNegatesResult() {
        // Given a filter that accepts a specific specification
        JnrTestRunnableSpecification additionSpec = mock(JnrTestRunnableSpecification.class);
        when(additionSpec.description()).thenReturn("Addition");
        
        JnrTestSpecificationFilter originalFilter = spec -> 
            spec.description().equals("Addition");
        
        // When we negate it
        JnrTestSpecificationFilter negatedFilter = JnrTestFilters.notSpecification(originalFilter);
        
        // Then the results should be inverted
        assertTrue(originalFilter.include(additionSpec));
        assertFalse(negatedFilter.include(additionSpec));
        
        JnrTestRunnableSpecification subtractionSpec = mock(JnrTestRunnableSpecification.class);
        when(subtractionSpec.description()).thenReturn("Subtraction");
        
        assertFalse(originalFilter.include(subtractionSpec));
        assertTrue(negatedFilter.include(subtractionSpec));
    }
}
