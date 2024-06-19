def match_umbrella_person(umbrellas, persons):
    matched_pairs = []
    
    for umbrella in umbrellas:
        min_dist = float('inf')
        matched_person = None
        
        for person in persons:
            ux, uy = umbrella['center']
            px, py = person['center']
            dist = ((ux - px) ** 2 + (uy - py) ** 2) ** 0.5
            
            if dist < min_dist:
                min_dist = dist
                matched_person = person
        
        if matched_person:
            matched_pairs.append((umbrella, matched_person))
            persons.remove(matched_person)
    
    return matched_pairs
