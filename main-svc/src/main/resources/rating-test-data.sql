INSERT INTO users (id, name, email)
VALUES (101, 'Peter', 'peter@example.com'),
       (102, 'Kate', 'kate@example.com'),
       (103, 'Paul', 'paul@example.com'),
       (104, 'Elizabeth', 'betty@example.com'),
       (105, 'Anne', 'anne@example.com'),
       (106, 'Alex 1', 'alex1@example.com'),
       (107, 'Alex 2', 'alex2@example.com'),
       (108, 'Alex 3', 'alex3@example.com'),
       (109, 'Nick 1', 'nick1@example.com'),
       (110, 'Nick 2', 'nick2@example.com')
ON CONFLICT (id) DO UPDATE
    SET name  = excluded.name,
        email = excluded.email;

INSERT INTO categories (id, name)
VALUES (101, 'Music'),
       (102, 'Art'),
       (103, 'Theater'),
       (104, 'Tours')
ON CONFLICT (id) DO UPDATE
    SET name = excluded.name;

INSERT INTO locations (id, lat, lon)
VALUES (101, 85.3388977050781, -170.733505249023),
       (102, 87.9150009155273, 114.153701782226),
       (103, -35.4953994750976, 56.9607009887695),
       (104, -25.6296005249023, 11.8270998001098),
       (105, -24.9414005279541, 171.958297729492),
       (106, 69.370002746582, -169.933898925781),
       (107, 38.9235000610351, 159.810302734375),
       (108, -78.6011962890625, 133.481399536132),
       (109, -84.9757995605468, -122.489501953125),
       (110, 10.5384998321533, 78.7559967041015),
       (111, 45.9754981994628, -7.70149993896484)
ON CONFLICT (id) DO UPDATE
    SET lat = excluded.lat,
        lon = excluded.lon;

INSERT INTO events (id, title, category_id, paid, event_date, initiator_id,
                    participant_limit, state, created_on, published_on,
                    location_id, request_moderation, confirmed_requests,
                    views, annotation, description)
VALUES (101, 'Peter''s Event 1', 101, true, NOW() - INTERVAL '1 day 2 hours 30 minutes',
        101, 0, 'PUBLISHED', NOW() - INTERVAL '7 days 2 hours 30 minutes',
        NOW() - INTERVAL '7 days 1 hour 30 minutes', 101, false, 0, 957,
        'Peter''s Event 1 annotation. Occaecati dicta temporibus. Qui et in et modi dignissimos et atque.',
        'Peter''s Event 1 description. Cumque enim non eius corrupti accusantium fugiat odio. Quisquam in ' ||
        'velit maiores ratione similique architecto provident quasi ea. Quibusdam dicta mollitia.'),
       (102, 'Peter''s Event 2', 102, false, NOW() - INTERVAL '5 days 4 hours', 101,
        0, 'PUBLISHED', NOW() - INTERVAL '6 days 4 hours',
        NOW() - INTERVAL '6 days 3 hours', 102, false, 0, 1023,
        'Peter''s Event 2 annotation. Eveniet qui ducimus sit neque est distinctio repudiandae qui.',
        'Peter''s Event 2 description. Eveniet qui ducimus sit neque est distinctio repudiandae qui. ' ||
        'Nemo possimus accusamus omnis omnis ducimus quisquam enim. Distinctio dolores aliquid ut vero quis. ' ||
        'Aut dolorem voluptatum ut fugit quas mollitia eligendi.'),
       (103, 'Kate''s Event 1', 101, true, NOW() - INTERVAL '4 days 4 hours', 102,
        0, 'PUBLISHED', NOW() - INTERVAL '5 days 4 hours',
        NOW() - INTERVAL '5 days 3 hours', 103, false, 0, 1032,
        'Kate''s Event 1 annotation. Vel amet laudantium et nostrum. ' ||
        'Exercitationem quos qui sunt animi.',
        'Kate''s Event 1 description. Vel amet laudantium et nostrum. ' ||
        'Exercitationem quos qui sunt animi. Perspiciatis et fugit facere ' ||
        'non ducimus vel rerum. Eius vel voluptatem provident quas cumque. ' ||
        'Atque corrupti hic perspiciatis.'),
       (104, 'Kate''s Event 2', 103, true, NOW() - INTERVAL '3 days 4 hours', 102,
        0, 'PUBLISHED', NOW() - INTERVAL '4 days 4 hours',
        NOW() - INTERVAL '4 days 3 hours', 104, false, 0, 786,
        'Kate''s Event 2 annotation. Quia architecto quo deserunt non id id sequi.',
        'Kate''s Event 2 description. Quia architecto quo deserunt non id id sequi. ' ||
        'Accusantium cum nobis culpa voluptatem libero quis. Totam non quibusdam. ' ||
        'Consequatur voluptate eius maxime perferendis cum.'),
       (105, 'Peter''s Event 3', 104, true, NOW() - INTERVAL '2 days 4 hours', 101,
        1, 'PUBLISHED', NOW() - INTERVAL '3 days 4 hours',
        NOW() - INTERVAL '3 days 3 hours', 105, true, 1, 23,
        'Peter''s Private Event annotation. Et omnis molestiae eius cum molestiae ea quas. ' ||
        'Aut sed est doloribus assumenda dolorem est dignissimos et rerum.',
        'Peter''s Private Event description. Et omnis molestiae eius cum molestiae ea quas. ' ||
        'Aut sed est doloribus assumenda dolorem est dignissimos et rerum. Voluptatem ' ||
        'nobis architecto voluptatem deleniti ipsam officiis. Unde odit quam dolorem nihil'),
       (106, 'Peter''s Pending Event', 102, false, NOW() - INTERVAL '1 day 4 hours', 101,
        0, 'PENDING', NOW() - INTERVAL '2 days 4 hours', null, 106, false, 0, 0,
        'Peter''s Pending Event annotation. Autem nihil sunt. Ex praesentium corrupti ' ||
        'incidunt voluptatem dignissimos pariatur sunt repellat.',
        'Peter''s Pending Event description. Autem nihil sunt. Ex praesentium corrupti ' ||
        'incidunt voluptatem dignissimos pariatur sunt repellat. Eos dolores veritatis ' ||
        'excepturi laborum. Voluptatibus inventore cupiditate ut voluptatum voluptate ut. ' ||
        'Distinctio fugiat molestiae sunt ut suscipit sunt. Nobis id id.'),
       (107, 'Kate''s Canceled Event', 104, false, NOW() - INTERVAL '1 day', 102,
        0, 'CANCELED', NOW() - INTERVAL '1 day 4 hours', null, 107, false, 0, 0,
        'Kate''s Canceled Event annotation. Tempore debitis qui unde. Nisi ut sit placeat.',
        'Kate''s Canceled Event description. Tempore debitis qui unde. Nisi ut sit ' ||
        'placeat. Reprehenderit quibusdam inventore nobis animi. Ab ipsam voluptate vel ' ||
        'voluptatibus quibusdam sit necessitatibus itaque.'),
       (108, 'Peter''s Future Event', 103, true, NOW() + INTERVAL '1 day', 101,
        0, 'PUBLISHED', NOW() - INTERVAL '1 day 2 hours',
        NOW() - INTERVAL '1 day 1 hour', 108, false, 0, 130,
        'Peter''s Future Event annotation. Provident quia quidem. Amet quia enim.',
        'Peter''s Future Event description. Provident quia quidem. Amet quia enim. ' ||
        'Corrupti cum et qui id distinctio. Quia voluptatem quam totam impedit quasi. ' ||
        'Earum est pariatur ipsum et dolores reprehenderit eaque. Eligendi non nobis ' ||
        'qui qui optio.'),
       (109, 'Kate''s Future Event', 102, false, NOW() + INTERVAL '2 days', 102,
        0, 'PUBLISHED', NOW() - INTERVAL '12 hours',
        NOW() - INTERVAL '11 hours', 109, false, 0, 189,
        'Kate''s Future Event annotation. Sapiente error et ab dignissimos. ' ||
        'Quod ipsum quia consectetur sit natus vero consequatur deleniti qui.',
        'Kate''s Future Event description. Sapiente error et ab dignissimos. ' ||
        'Quod ipsum quia consectetur sit natus vero consequatur deleniti qui. ' ||
        'Vitae eos dolorem explicabo totam. Dolor qui consequatur voluptatibus ' ||
        'quos recusandae voluptatem expedita. Atque dolore magni cumque.'),
       (110, 'Paul''s Event 1', 101, false, NOW() - INTERVAL '4 days 2 hours', 103,
        0, 'PUBLISHED', NOW() - INTERVAL '5 days 2 hours',
        NOW() - INTERVAL '5 days 1 hours', 110, false, 0, 234,
        'Paul''s Event 1 annotation. Occaecati dicta temporibus. Qui et in et ' ||
        'modi dignissimos et atque.',
        'Paul''s Event 1 description. Vel amet laudantium et nostrum. ' ||
        'Exercitationem quos qui sunt animi. Perspiciatis et fugit facere non ' ||
        'ducimus vel rerum. Eius vel voluptatem provident quas cumque. Atque corrupti ' ||
        'hic perspiciatis.'),
       (111, 'Paul''s Event 2', 103, true, NOW() - INTERVAL '3 days 2 hours', 103,
        0, 'PUBLISHED', NOW() - INTERVAL '4 days 2 hours',
        NOW() - INTERVAL '4 days 1 hours', 111, false, 0, 628,
        'Paul''s Event 2 annotation. Ex praesentium corrupti incidunt voluptatem ' ||
        'dignissimos pariatur sunt repellat.',
        'Paul''s Event 2 description. Corrupti cum et qui id distinctio. ' ||
        'Quia voluptatem quam totam impedit quasi. Earum est pariatur ipsum et dolores ' ||
        'reprehenderit eaque. ')

ON CONFLICT (id) DO UPDATE
    SET title              = excluded.title,
        category_id        = excluded.category_id,
        paid               = excluded.paid,
        event_date         = excluded.event_date,
        initiator_id       = excluded.initiator_id,
        participant_limit  = excluded.participant_limit,
        state              = excluded.state,
        created_on         = excluded.created_on,
        published_on       = excluded.published_on,
        location_id        = excluded.location_id,
        request_moderation = excluded.request_moderation,
        confirmed_requests = excluded.confirmed_requests,
        annotation         = excluded.annotation,
        description        = excluded.description;

INSERT INTO requests (id, event_id, requester_id, created, status)
VALUES (101, 105, 104, NOW() - INTERVAL '3 days', 'CONFIRMED'),
       (102, 105, 103, NOW() - INTERVAL '2 days 23 hours', 'REJECTED')
ON CONFLICT (id) DO UPDATE
    SET event_id     = excluded.event_id,
        requester_id = excluded.requester_id,
        created      = excluded.created,
        status       = excluded.status;