/**
 * Utility function to trim whitespace from the start and end of all string values in an object
 * Can handle nested objects and arrays
 * @param {Object} obj - The object containing values to trim
 * @return {Object} - A new object with all string values trimmed
 */
function trimObjectValues(obj) {
    // Handle null or undefined
    if (obj === null || obj === undefined) {
        return obj;
    }
    
    // Handle arrays
    if (Array.isArray(obj)) {
        return obj.map(item => trimObjectValues(item));
    }
    
    // Handle objects
    if (typeof obj === 'object') {
        const result = {};
        for (const key in obj) {
            if (obj.hasOwnProperty(key)) {
                result[key] = trimObjectValues(obj[key]);
            }
        }
        return result;
    }
    
    // Handle string values - trim whitespace
    if (typeof obj === 'string') {
        return obj.trim();
    }
    
    // Return unchanged for non-string primitives
    return obj;
}
