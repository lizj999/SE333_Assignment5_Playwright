# Task 4 — Reflection: Manual vs AI-Assisted UI Testing

## Manual UI Testing (Java + Playwright)

Writing the Playwright tests manually in Java required a deep understanding of 
the application's structure and behavior. Each interaction whether that was searching for a 
product, applying filters, filling out forms, and asserting page content, had 
to be coded using Playwright's Java API. This process was time-consuming 
because locating the correct CSS selectors and ARIA roles required trial and error, 
especially for elements inside iframes or elements with ambiguous selectors that 
matched multiple elements on the page. For example, the "Proceed As Guest" button 
was embedded inside a login iframee which required inspecting the page structure 
carefully. Similarly, price values changed between test runs, requiring constant 
maintenance of the assertions.

## AI-Assisted UI Testing (Playwright MCP Agent)

Using the Playwright MCP agent to generate tests through natural language prompts 
was way faster for initial test creation. Describing the workflow in plain 
English, such as "search for earbuds, filter by JBL and Black, add to cart, and 
verify the cart shows 1 item", allowed the agent to generate runnable Java/JUnit 
code almost instantly. The generated code closely resembled what the Playwright 
codegen recorder produces. However, the AI-generated tests still required manual review and 
adjustment, particularly for prices and iframes. The agent also 
occasionally produced selectors that matched multiple elements whcih caused strict mode 
violations similar to those encountered in manual testing.

## Comparison

In terms of ease of writing, AI-assisted testing has a clear advantage. What took 
hours of debugging manually was scaffolded in minutes. However, the accuracy and 
reliability of generated tests depends heavily on how precisely the workflow is 
described. Both approaches struggled with the same real-world challenges: dynamic 
content, iframes, and ambiguous selectors. Maintenance effort is comparable since 
both require updates when the UI changes. The key limitation of AI-assisted testing 
is that it still requires a developer to understand Playwright well enough to verify, 
debug, and fix the generated code. Overall, AI-assisted testing is best used as a 
starting point that accelerates development, while manual testing provides the 
precision needed for complex flows.